# SkinAI - AI-Powered Skincare Recommendation System

An intelligent skincare recommendation system that combines **computer vision**, **machine learning**, and **large language models (LLMs)** to provide personalized skincare advice based on facial analysis and user preferences.

The application analyzes facial images to detect common skin concerns, generates personalized recommendations and suggests skincare products tailored to each user's needs.

## Features

- 🔍 **AI Facial Analysis**
  - Detects multiple skin conditions from a facial image.
  - Uses an EfficientNetV2-based multi-label classification model.
  - Supports detection of:
    - Acne
    - Hyperpigmentation
    - Wrinkles
    - Dark circles
    - Redness
    - Dry skin
    - Oily skin
    - Normal skin

- 🤖 **LLM-Powered Recommendations**
  - Generates personalized skincare routines.
  - Explains detected skin conditions.
  - Recommends suitable ingredients and skincare practices.
  - Answers skincare-related questions through an AI assistant.

- 🧴 **Product Recommendation Engine**
  - Matches products to detected skin concerns.
  - Considers ingredient compatibility.
  - Avoids products containing unsuitable ingredients.
  - Provides recommendation explanations.

- 👤 **User Management**
  - User registration and authentication.
  - Secure login using JWT.
  - Profile management.

- ❤️ **Favorites**
  - Save preferred skincare products.
  - Remove products from favorites.
  - View saved products.

- 📱 **Modern Mobile Application**
  - Built with Jetpack Compose.
  - Responsive and intuitive user interface.
  - Material Design components.

---

## Architecture

The project consists of two main components:

### Mobile Application
- Kotlin
- Jetpack Compose
- MVVM Architecture
- Retrofit
- Navigation Compose

### Backend
- Spring Boot
- Kotlin
- PostgreSQL
- JWT Authentication
- REST API
- TensorFlow model integration
- Groq LLM API integration

---

## AI Model

The facial analysis model is based on **EfficientNetV2** and performs **multi-label image classification**.

### Training

- Framework: TensorFlow / Keras
- Input size: **224 × 224**
- Loss function: Binary Crossentropy
- Optimizer: Adam
- Transfer Learning with EfficientNetV2

### Evaluation Metrics

The model was evaluated using:

- Accuracy
- Precision
- Recall
- F1-score

---

## Technologies Used

### Frontend

- Kotlin
- Jetpack Compose
- Material 3
- Retrofit
- Coil
- Hilt
- Coroutines
- ViewModel

### Backend

- Spring Boot
- Kotlin
- Spring Security
- JWT
- PostgreSQL
- JPA / Hibernate
- TensorFlow
- Groq API

### Machine Learning

- Python
- TensorFlow
- Keras
- EfficientNetV2
- NumPy
- Pandas
- Scikit-learn

---

## Project Structure

```
Mobile App
│
├── UI
├── ViewModels
├── Repository
├── Networking
├── Models
└── Navigation

Backend
│
├── Controllers
├── Services
├── Repositories
├── Entities
├── Security
├── AI Integration
└── Recommendation Engine

Machine Learning
│
├── Dataset
├── Training Scripts
├── Model
└── Evaluation
```

---

## How It Works

1. The user uploads a facial image.
2. The AI model analyzes the image and detects skin conditions.
3. The backend processes the prediction results.
4. The recommendation engine filters suitable skincare products.
5. The LLM generates personalized skincare advice.
6. The user receives:
   - Detected skin concerns
   - Personalized skincare routine
   - Product recommendations
   - AI-generated explanations

---

## Installation

### Backend

```bash
git clone https://github.com/yourusername/skincare-ai.git

cd backend

./gradlew bootRun
```

---

### Mobile Application

Open the Android project in **Android Studio** and run:

```
Run > Run 'app'
```

---

## Environment Variables

Create an `.env` file or configure the following properties:

```
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=

JWT_SECRET=

GROQ_API_KEY=

MODEL_PATH=
```

---

## Future Improvements

- Push notifications
- Progress tracking over time
- Skin evolution comparison
- OCR support for cosmetic ingredient lists
- Barcode scanning
- Product availability integration
- Multi-language support

---

## License

This project was developed as a Bachelor's Thesis and is intended for educational and research purposes.

---

## Author

**Ciorita Elena Alexandra**

Bachelor's Thesis

Faculty of Mathematics and Computer Science

University of Bucharest
