java -jar ./signapk.jar platform.x509.pem platform.pk8 C:/Users/lenovo/Desktop/MyApplicationDrink/app/build/outputs/apk/debug/app-debug.apk a.apk
adb install -r ./a.apk
adb shell  am   start   trash_can.jashshor.myapplication/trash_can.jashshor.myapplication.MainActivity