# ShareLocation

# Project overview:
* As a User, anyone can create new circle as well as  join in circle that already existing .
* Members of a circle can view each other's real-time location ,address and distance in miles on a map.
* Circle member will get notification if anyone in his/her circle enter or exit particular Geofence.
* Anytime user can leave the circle if they want.
* Any User can be a Driver or  a Rider.

## Libraries Used:

* **Amazon's Parse server**:As a backend server,it stores data in the form of a data model.
* **Parse live query**:To listen for live updates from server.
* **Firebase cloud messaging**:To send and receive push notification.
* **Firebase crashlytics**:To obtain a detailed crash report for the purpose of debugging.
* **Retrofit**:Used to handle network request and response.
* **Gson converter**:For data serialization and deserialization when communicate with REST Api.
* **Data Binding**:bind observable data to UI elements and to avoid boilerplate code like findViewById().
* **ViewModel**: Store UI-related data that isn't destroyed on app rotations.
* **LiveData**:Build data objects that notify views when the underlying database changes.
* **Lifecycles extention**: Create a UI that automatically responds to lifecycle events.
* **Hilt**:DI library to write clean,testable and maitainable code.
* **picasso**:To load image from network into imageview.
* **JUnit**:For Unit testing.
* **Mockito**:Mocking libriry used for Unit testing.
* **Robolectric**:To test application without emulator.

## API Used:
* **Google Maps API**:Users can see the real-time location and distance of circle members on the map using Google Maps API.
* **Google's Distance API**:The Distance Matrix API is a service that provides travel distance and time for a matrix of origins and destinations. The API returns information based on the recommended route between start and end points, as calculated by the Google Maps API.
* **Roads API**:The Roads API takes up to 100 GPS points collected along a route, and returns a similar set of data, with the points snapped to the most likely roads the vehicle was traveling along. 



## The app has following packages:
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
 
