





Parse.Cloud.define("driverStartedFromOrigin", async (request) => {



  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channels);


  var driverName=request.params.driverName;
  var placeName=request.params.placeName;
 // var originName=request.params.originName;

  var time=request.params.time;

 return Parse.Push.send({
            where:query,

         data: {
             title:"Driver Started  ",
             alert:driverName +" started from "+placeName +" at "+time ,
         }
     }, { useMasterKey: true });

 });



Parse.Cloud.define("driverStartedFromDestination", async (request) => {



  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channels);


  var driverName=request.params.driverName;
  var placeName=request.params.placeName;
 // var originName=request.params.originName;

  var time=request.params.time;

 return Parse.Push.send({
            where:query,

         data: {
             title:"Driver Started  ",
             alert:driverName +" started from "+placeName +" at "+time ,
         }
     }, { useMasterKey: true });

 });


Parse.Cloud.define("driverArrivedOrigin", async (request) => {



  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channels);


  var driverName=request.params.driverName;
  var placeName=request.params.placeName;
 // var originName=request.params.originName;

  var time=request.params.time;

 return Parse.Push.send({
            where:query,

         data: {
             title:"Driver Arrived  ",
             alert:driverName +" arrived "+placeName +" at "+time ,
         }
     }, { useMasterKey: true });

 });


Parse.Cloud.define("driverArrivedDestination", async (request) => {



  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channels);


  var driverName=request.params.driverName;
  var placeName=request.params.placeName;
 // var originName=request.params.originName;

  var time=request.params.time;

 return Parse.Push.send({
            where:query,

         data: {
             title:"Driver Arrived  ",
             alert:driverName +" arrived "+placeName +" at "+time ,
         }
     }, { useMasterKey: true });

 });



Parse.LiveQuery.on('error', (error) => {
  console.log(error);
});

Parse.Cloud.onLiveQueryEvent(({
  event,
  client,
  sessionToken,
  useMasterKey,
  installationId,
  clients,
  subscriptions,
  error
}) => {
  if (event !== 'ws_disconnect') {
    return;

  }


console.log('cloud called')


  // Do your magic
});








Parse.Cloud.define("arrivedPush", async (request) => {

//  const query = new Parse.Query(Parse.User);

 // query.equalTo("objectId", request.params.adminId);

  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channels);
 //query.notEqualTo("deviceToken", request.params.deviceToken);

  var userName=request.params.userName;
  var userLocation=request.params.userLocation;
  var time=request.params.time;

 return Parse.Push.send({
            where:query,

         data: {
            title:"Arrived",
             alert:userName +" arrived "+userLocation+ " at " +time ,
         }
     }, { useMasterKey: true });

 });


Parse.Cloud.define("leftPush", async (request) => {

//  const query = new Parse.Query(Parse.User);

 // query.equalTo("objectId", request.params.adminId);

  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channels);
 //query.notEqualTo("deviceToken", request.params.deviceToken);

  var userName=request.params.userName;
  var userLocation=request.params.userLocation;

  var time=request.params.time;

 return Parse.Push.send({
            where:query,

         data: {
             title:"Left",
             alert:userName +" left from "+userLocation +" at "+time ,
         }
     }, { useMasterKey: true });

 });




Parse.Cloud.define("newMemberAddedPush", async (request) => {

  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channel);

 query.notEqualTo("deviceToken", request.params.deviceToken);

  var newMemberName=request.params.newMemberName;
  //var circleName=request.params.channel;
 var circleName=request.params.circleName;

 return Parse.Push.send({
            where:query,

         data: {
 title:"New member Joined in your Circle",
             alert:newMemberName +" joined in "+circleName+" Circle",
         }
     }, { useMasterKey: true });

 }

  );




Parse.Cloud.define("memberLeftPush", async (request) => {

  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channel);
query.notEqualTo("deviceToken", request.params.deviceToken);

  var leftMemberName=request.params.leftMemberName;
  //var circleName=request.params.channel;
 var circleName=request.params.circleName;

 return Parse.Push.send({
            where:query,

         data: {
          title:"Member left from Circle",

             alert:leftMemberName +" left from "+circleName+" Circle",
         }
     }, { useMasterKey: true });

 }

  );


Parse.Cloud.define("sendDriverMessage", async (request) => {

  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channel);


  var driverName=request.params.driverName;

 var driverMessage=request.params.driverMessage;

 return Parse.Push.send({
            where:query,

         data: {
          title:"Message From Driver "+driverName,

             alert:driverMessage,
         }
     }, { useMasterKey: true });

 }

  );



Parse.Cloud.define("driverLeft", async (request) => {



  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channels);


  var userName=request.params.userName;
  var busStop=request.params.busStopName;

  var time=request.params.time;

 return Parse.Push.send({
            where:query,

         data: {
             title:"Driver Left",
             alert:userName +" left from "+busStop +" at "+time ,
         }
     }, { useMasterKey: true });

 });



Parse.Cloud.define("driverArrived", async (request) => {



  const query = new Parse.Query(Parse.Installation);

   query.equalTo("channels", request.params.channels);


  var userName=request.params.userName;
  var busStop=request.params.busStopName;

  var time=request.params.time;

 return Parse.Push.send({
            where:query,

         data: {
             title:"Driver Arrived",
             alert:userName +" Arrived "+busStop +" at "+time ,
         }
     }, { useMasterKey: true });

 });







