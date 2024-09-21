import subprocess
import sys

# 패키지가 설치되지 않았을 경우 설치하는 함수
def install_package(package):
    subprocess.check_call([sys.executable, "-m", "pip", "install", package])

# 필요한 패키지 목록
required_packages = ["openai", "python-dotenv", "fastapi", "requests", "uvicorn", "python-multipart"]

# 패키지 설치 여부 확인 후 설치
for package in required_packages:
    try:
        __import__(package)
    except ImportError:
        print(f"{package} 패키지가 설치되어 있지 않습니다. 설치를 진행합니다.")
        install_package(package)

from fastapi import FastAPI, File, UploadFile
import os
from dotenv import load_dotenv
import requests
import json
import uuid
from openai import OpenAI
import time
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

# FastAPI 앱 생성
app = FastAPI()

# CORS 설정 추가
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:7777"],  # 허용할 출처 (프론트엔드가 있는 도메인)
    allow_credentials=True,
    allow_methods=["*"],  # 모든 HTTP 메소드 허용
    allow_headers=["*"],  # 모든 헤더 허용
)

# .env 파일 로드
load_dotenv()

# 환경 변수 가져오기
clova_api_url = os.getenv("CLOVA_API_URL")
clova_secret_key = os.getenv("CLOVA_OCR_SECRET_KEY")
openai_api_key = os.getenv("OPENAI_API_KEY")
youtube_api_key = os.getenv("YOUTUBE_API_KEY")

def call_clova_ocr(image_file):
    print("CLOVA OCR 호출 시작")
    request_json = {
        'images': [{'format': 'jpg', 'name': 'receipt'}],
        'requestId': str(uuid.uuid4()),
        'version': 'V2',
        'timestamp': int(round(time.time() * 1000))
    }

    headers = {'X-OCR-SECRET': clova_secret_key}
    files = [('file', image_file)]

    response = requests.post(clova_api_url, headers=headers, data={'message': json.dumps(request_json)}, files=files)

    if response.status_code == 200:
        ocr_result = response.json()
        print("OCR 결과:", ocr_result)  # 로그 추가
        return ''.join([field['inferText'] for field in ocr_result['images'][0]['fields']])
    else:
        print(f"Error: {response.status_code}")  # 오류 로그 추가
        raise Exception(f"Error: {response.status_code}")

def call_openai_food_and_expiration(text):
    print("OpenAI 호출 시작 - 식품 및 소비기한 추출")
    client = OpenAI(api_key=openai_api_key)
    ocr_response = client.chat.completions.create(
        model="gpt-3.5-turbo-1106",
        messages=[
            {"role": "system",
             "content": "Extract only food items and ingredients from the text, ignoring non-food items such as clothing, household items, and others. Return only the names of food products."},
            {"role": "user", "content": text}
        ]
    )

    food_items = list(map(lambda x: x.replace('-', '').strip(), ocr_response.choices[0].message.content.splitlines()))
    print("추출된 식품 목록:", food_items)  # 로그 추가

    # 소비기한 추출
    if food_items:
        food_items_str = ', '.join(food_items)
        print("소비기한 추출 대상:", food_items_str)  # 로그 추가
        expiration_response = client.chat.completions.create(
            model="gpt-3.5-turbo-1106",
            messages=[
                {"role": "system",
                 "content": "For each food item listed, provide only the expiration date in days. No additional text is needed. For fresh products like milk and fruits, assign shorter expiration dates (e.g., 5), but for dry foods like snacks, ramen, frozen foods, jelly, and chocolate, assign longer expiration dates."},
                {"role": "user", "content": food_items_str}
            ]
        )

        expiration_dates_raw = expiration_response.choices[0].message.content.replace('\n', ',')
        print("추출된 소비기한:", expiration_dates_raw)  # 로그 추가
        expiration_dates = [int(x.strip()) if x.strip().isdigit() else 0 for x in expiration_dates_raw.split(',')]

        # 결과 결합
        result = dict(zip(food_items, expiration_dates))
        print("최종 결과:", result)  # 로그 추가
    else:
        result = {}
        print("추출된 식품이 없습니다.")  # 로그 추가

    return result

@app.post("/ocr/")
async def upload_file(file: UploadFile = File(...)):
    print("파일 업로드 시작")  # 로그 추가
    ocr_text = call_clova_ocr(file.file)
    combined_result = call_openai_food_and_expiration(ocr_text)
    print("OCR 및 OpenAI 결과:", combined_result)  # 로그 추가
    return combined_result

class RecipeRequest(BaseModel):
    allergies: list
    ingredients: list

def call_openai_for_recommend(allergies, ingredients):
    print("OpenAI 호출 시작 - 요리법 추천")  # 로그 추가
    recommend_prompt = f"""
        사용자는 다음과 같은 음식 알레르기가 있습니다: {', '.join(allergies)}.
        사용 가능한 재료는 다음과 같습니다: {', '.join(ingredients)}.

        이 정보를 바탕으로 알레르기를 피하고, 주어진 재료를 활용한 요리법을 한국어로 추천해주세요.
        요리법은 정중하고 자세하게 설명해주세요.

        아래 형식에 맞춰 JSON 형식으로 요리법을 제공해주세요:

        {{
            "food_name": "요리 이름",
            "recipe": "1. 첫 번째 단계\\n2. 두 번째 단계\\n3. 세 번째 단계\\n...",
            "comment": "알레르기를 고려한 요리 설명"
        }}
        """

    client = OpenAI(api_key=openai_api_key)

    # OpenAI API 호출
    response = client.chat.completions.create(
        model="gpt-3.5-turbo",
        messages=[
            {"role": "system", "content": "You are a helpful chef."},
            {"role": "user", "content": recommend_prompt}
        ]
    )

    # OpenAI의 응답 받기
    recipe_recommendation = response.choices[0].message.content
    print("추천된 요리법:", recipe_recommendation)  # 로그 추가
    return recipe_recommendation

from fastapi import FastAPI, HTTPException
import logging

# FastAPI 앱 생성
app = FastAPI()

# 로거 설정
logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

@app.post("/recipe-recommend/")
async def recipe_recommend(request: RecipeRequest):
    try:
        logger.debug("요리법 추천 요청 수신: %s", request.dict())

        # 요리법 추천 호출
        recipe_result = call_openai_for_recommend(request.allergies, request.ingredients)
        logger.debug("추천된 요리법 결과: %s", recipe_result)

        # JSON 문자열을 파싱하여 Python 객체로 변환
        try:
            recipe_result = json.loads(recipe_result)
        except json.JSONDecodeError as e:
            logger.error("추천된 요리 JSON 형식이 올바르지 않습니다: %s", str(e))
            raise HTTPException(status_code=400, detail="추천된 요리 JSON 형식이 올바르지 않습니다.")

        # JSON 객체에서 요리 이름 추출
        food_name = recipe_result.get("food_name", "")
        if not food_name:
            logger.error("추천된 요리 이름이 없습니다.")
            raise HTTPException(status_code=400, detail="추천된 요리 이름이 없습니다.")

        # YouTube에서 요리 이름으로 영상 검색
        video_info = search_youtube_video(food_name)
        logger.debug("유튜브 검색 결과: %s", video_info)

        return {
            "recipe": recipe_result,
            "youtube_video": video_info
        }

    except HTTPException as http_err:
        logger.error("HTTP 오류 발생: %s", http_err.detail)
        raise http_err

    except Exception as e:
        logger.error("서버 내부 오류 발생: %s", str(e))
        raise HTTPException(status_code=500, detail="Internal Server Error")

def search_youtube_video(food_name):
    print("YouTube 검색 시작:", food_name)  # 로그 추가
    search_url = "https://www.googleapis.com/youtube/v3/search"
    params = {
        "part": "snippet",
        "q": food_name,
        "key": youtube_api_key,
        "type": "video",
        "maxResults": 1,
        "order": "viewCount"  # 인기 순으로 정렬
    }
    response = requests.get(search_url, params=params)
    if response.status_code == 200:
        video_data = response.json()
        print("YouTube 검색 응답:", video_data)  # 로그 추가
        if video_data["items"]:
            video_id = video_data["items"][0]["id"]["videoId"]
            video_title = video_data["items"][0]["snippet"]["title"]
            video_url = f"https://www.youtube.com/watch?v={video_id}"
            print("검색된 영상:", video_title, video_url)  # 로그 추가
            return {"title": video_title, "url": video_url}
        else:
            print("관련 영상을 찾을 수 없습니다.")  # 로그 추가
            return {"title": "관련 영상을 찾을 수 없습니다.", "url": ""}
    else:
        print(f"Error: {response.status_code}")  # 오류 로그 추가
        raise Exception(f"Error: {response.status_code}")
