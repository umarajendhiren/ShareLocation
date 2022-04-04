# ShareLocation
**The app has following packages:**
* **BackgroundLocationUpdate**:Contains BroadcastReceiver to receive broadcasts from system and service class to run backround service.
* **DistanceApiClient**:Contains Retrofit service class to communicate with Google's Diatance Api.
* **PojoForGSONConverter**:Contains model classes for Distance Api.Gson converter uses this classes for serialization and deserialization.
* **SnapToRoadApiClient**:Contains Retrofit service class to communicate with Google's SnapToRoadApi Api.
* **adapter**:Contains Recyclerviewadapters to bind data to views and binding adapters for databinding.
* **hiltDI**:Contains Application module class has dependency providing classes for Hilt.
* **model:** Contains basic POJO classes.
* **repository:** Contains repository class handles data operation and provide clean API to the Viewmodel.
* **ui**:Contains View classes.This is responsible for drawing data to the screen.   
* **viewmodel**:Contains viewmodel for view.This is responsible for holding and processing all the data needed for UI.
* **utilities**:Contains utility classes.
 
