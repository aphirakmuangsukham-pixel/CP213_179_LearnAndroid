# 🧠 FlashCard App (LearnAndroid)

แอปพลิเคชัน Android สำหรับการเรียนรู้และจดจำคำศัพท์ด้วย **Flashcard** ที่ออกแบบมาให้ทันสมัย ใช้งานง่าย พร้อมฟีเจอร์ที่ช่วยกระตุ้นการเรียนรู้ เช่น โหมดเกมจับเวลา ระบบสถิติ และการใช้ AI ในการช่วยสร้างเนื้อหา

## 🌟 ภาพรวม (Overview)
โปรเจกต์นี้เป็นแอปพลิเคชันบนมือถือระบบ Android ที่พัฒนาด้วยภาษา **Kotlin** และ **Jetpack Compose** ผู้ใช้สามารถสร้างหมวดหมู่คำศัพท์ (Deck) ของตัวเอง เพิ่มการ์ดคำศัพท์ (พร้อมแนบรูปภาพ) และเรียนรู้ผ่านระบบ Flashcard ปกติ หรือท้าทายตัวเองในโหมด Time Attack นอกจากนี้ยังมีการนำ **Google Gemini AI** มาช่วยในการสร้าง Flashcard อัตโนมัติอีกด้วย

## ✨ ฟีเจอร์หลัก (Key Features)
* **Flashcard Management**: สร้าง ลบ แก้ไข หมวดหมู่ (Category) และการ์ดคำศัพท์
* **Rich Content**: รองรับการใส่ข้อความทั้งด้านหน้าและด้านหลัง พร้อมแนบรูปภาพประกอบลงในการ์ดได้
* **Study Mode**: โหมดทบทวนคำศัพท์มาตรฐานแบบแตะเพื่อพลิกการ์ด (Flip Card)
* **Challenge Mode (Time Attack)**: โหมดมินิเกมจับเวลาที่ช่วยเพิ่มความท้าทายและความสนุกในการจำคำศัพท์
* **AI Generator**: เชื่อมต่อกับ Google Gemini AI เพื่อช่วยคิดและสร้าง Flashcard ใหม่ๆ ให้โดยอัตโนมัติ
* **Learning Analytics**: หน้า Dashboard แจ้งสถิติการเรียนรู้ เช่น วันที่เรียนต่อเนื่อง (Streak), จำนวนการ์ดที่ทบทวนในแต่ละวัน และเปอร์เซ็นต์ความแม่นยำ (Accuracy)
* **Smart Reminders**: ระบบตั้งเตือน (Notification) แจ้งเตือนให้ผู้ใช้กลับมาทบทวนคำศัพท์อย่างสม่ำเสมอ

## 🛠️ เทคโนโลยีที่ใช้ (Tech Stack)
* **Language**: Kotlin
* **UI Toolkit**: Jetpack Compose (Material 3)
* **Architecture**: MVVM (Model-View-ViewModel)
* **Local Database**: Room Database (SQLite)
* **Image Loading**: Coil
* **Navigation**: Navigation Compose
* **AI Integration**: Google Gemini SDK (`generativeai`)
* **Concurrency**: Kotlin Coroutines & Flow

## 📂 โครงสร้างไฟล์ (Project Structure)
โครงสร้างหลักของโปรเจกต์ภายใน `app/src/main/java/com/example/flashcard/` มีดังนี้:

```text
com.example.flashcard/
│
├── data/                       # ชั้นข้อมูล (Data Layer)
│   ├── local/                  # ฐานข้อมูลภายในเครื่อง (Room Database)
│   │   ├── AppDatabase.kt      # กำหนดการตั้งค่าฐานข้อมูล Room
│   │   ├── Daos.kt             # Data Access Objects สำหรับดึง/บันทึกข้อมูล
│   │   ├── Entities.kt         # โครงสร้างตารางในฐานข้อมูล (Category, FlashCard)
│   │   └── SettingsRepository.kt
│   └── remote/                 # ข้อมูลจาก API ภายนอก
│       └── AiFlashcardResponse.kt # Data class สำหรับรับข้อมูล JSON จาก Gemini AI
│
├── ui/                         # ชั้นการแสดงผล (UI Layer)
│   ├── screens/                # หน้าจอต่างๆ ภายในแอปพลิเคชัน
│   │   ├── HomeScreen.kt          # หน้าหลักและ Dashboard แสดงสถิติ
│   │   ├── CategoryDetailScreen.kt# หน้าจัดการ Flashcard ภายในหมวดหมู่นั้นๆ
│   │   ├── StudyScreen.kt         # หน้าจอโหมดการเรียนรู้ (ทบทวนคำศัพท์)
│   │   ├── ChallengeMenuScreen.kt # เมนูสำหรับเลือกโหมดและตั้งค่าก่อนเริ่มเกม
│   │   ├── ChallengeGameScreen.kt # หน้าจอเล่นเกมตอบคำศัพท์จับเวลา
│   │   └── SettingsScreen.kt      # หน้าตั้งค่าแอปพลิเคชัน
│   ├── theme/                  # จัดการเรื่องธีม สี และฟอนต์ของแอป
│   ├── notifications/          # จัดการระบบการแจ้งเตือน (ReminderReceiver)
│   └── AppNavigation.kt        # ระบบ Routing ควบคุมการเปลี่ยนหน้าของ Compose
│
├── FlashCardViewModel.kt       # ViewModel หลักที่เชื่อมโยง UI กับ Data
└── MainActivity.kt             # จุดเริ่มต้นการทำงานของแอปพลิเคชัน (Entry Point)
```

## 🚀 วิธีติดตั้งและรันโปรเจกต์ (Installation & Setup)

1. **โคลนโปรเจกต์ (Clone the repository)**
   ```bash
   git clone <repository_url>
   ```

2. **เปิดโปรเจกต์ด้วย Android Studio**
   - เปิดโปรแกรม Android Studio
   - เลือกเมนู `Open` และเลือกโฟลเดอร์โปรเจกต์ที่มีไฟล์ `build.gradle.kts` อยู่

3. **ตั้งค่า API Key สำหรับ Gemini AI**
   เพื่อเปิดใช้งานฟีเจอร์ AI คุณต้องมี Google Gemini API Key
   - สร้างไฟล์ชื่อ `local.properties` (หากยังไม่มี) ไว้ที่ Root directory ของโปรเจกต์
   - เพิ่มโค้ดด้านล่างนี้ลงในไฟล์ พร้อมใส่ API Key ของคุณ:
     ```properties
     GEMINI_API_KEY=ใส่_API_KEY_ของคุณที่นี่
     GEMINI_MODEL=gemini-2.0-flash
     ```

4. **ซิงค์ Gradle (Sync Project)**
   - รอให้ Android Studio ดาวน์โหลด Dependencies ต่างๆ จนเสร็จ หรือกดปุ่ม `Sync Project with Gradle Files`

5. **รันแอปพลิเคชัน (Run the App)**
   - เชื่อมต่ออุปกรณ์ Android หรือเปิด Emulator
   - กดปุ่ม **Run** (รูปสามเหลี่ยมสีเขียว) หรือกด `Shift + F10` เพื่อเริ่มรันแอปพลิเคชัน
