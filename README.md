# EvNabzı

EvNabzı, evdeki on akıllı cihazı oda bazında takip etmeyi ve açık cihazların anlık güç tüketimini görmeyi sağlayan bir Android uygulaması. Cihaz durumları ödevdeki veri kümesi üzerinden simüle edilir.

## Çalıştırma

Android Studio'da `week3_assignment` klasörünü açın. Gradle eşitlemesi için JDK 17 ve Android SDK 36'yı kullanın. API 24 veya üzeri bir cihaz ya da emülatör seçip `app` modülünü çalıştırın.

## Uygulama yapısı

- **Veri:** `HomeDeviceCatalog` ödevdeki sabit envanteri tanımlar. `InMemoryDeviceRepository` cihaz durumlarını `StateFlow` ile yayınlar ve çevrimdışı bir cihazın durumunun değiştirilmesini engeller.
- **ViewModel:** `EnergyDashboardViewModel`, cihaz durumunu, oda seçimini ve arama metnini `combine` ile birleştirir. Güç hesabı ve filtreleme `Dispatchers.Default` üzerinde çalışır. Arama metni anında güncellenir; liste, filtre sonucu hazır olunca güncellenir.
- **Arayüz:** `MainActivity` akışları `collectAsStateWithLifecycle` ile izler. `EnergyDashboard` görüntüleyeceği verileri parametre olarak alır ve kullanıcı işlemlerini callback'lerle ViewModel'e iletir. Cihaz satırları sabit kimliklerle bir `LazyColumn` içinde gösterilir. Oda menüsünün açık veya kapalı durumu da ViewModel tarafından yönetilir.

Sayaç, oda ve arama filtrelerinden bağımsız olarak evdeki tüm açık ve çevrimiçi cihazların toplam gücünü Watt cinsinden gösterir. Oda seçildiğinde o odanın açık ve çevrimiçi cihazlarının toplamı ayrıca gösterilir. Arama yalnızca görünen cihazları değiştirir. Cihaz satırındaki değer cihazın açıkken tükettiği nominal güçtür.

## Deneme senaryoları

| İşlem | Beklenen sonuç |
| --- | --- |
| Uygulamayı aç | 10 cihaz, 9 çevrimiçi cihaz ve **106 W** |
| Tüm odalar menüsünden Salon'u seç, Tavan Avizesi'ni aç | Salon'da 3 cihaz; ev toplamı **166 W**, Salon tüketimi **87 W** |
| Tavan Avizesi'ni yeniden kapat | Ev toplamı **106 W**, Salon tüketimi **27 W** |
| Tüm odalar seçiliyken `lambasi` ara | Köşe Lambası ve Çalışma Odası Lambası; sayaç değişmez |
| Arama temizken Balkon'u seç | Aydınlatma çevrimdışı, Aç düğmesi pasif |

ViewModel testlerini çalıştırmak için Windows'ta `gradlew.bat :app:testDebugUnitTest`, macOS veya Linux'ta `./gradlew :app:testDebugUnitTest` komutunu kullanın. Ekran yenileme hızı, cihaz ve emülatör ayarlarına bağlıdır.
