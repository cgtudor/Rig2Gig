const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

exports.Notifications = functions.firestore
  .document('communications/{uID}/received/{commId}')
  .onCreate((snap, context) => {
	const newComm = snap.data
  const uID = context.params.uID;
  const commID = context.params.commId;
	console.log('User to send notification', uID);

		const senderUser = snap.get("sent-from");
		const notificationMessage = snap.get("notification-message");
		const notificationTitle = snap.get("notification-title");

		const fromUser = admin.firestore().collection("users").doc(senderUser).get();
		const toUser = admin.firestore().collection("users").doc(uID).get();

		return Promise.all([fromUser, toUser]).then(result => {
			const fromUserName = result[0].get("given-name");
			const toUserName = result[1].get("given-name");
			const tokenId = result[1].get("token");

			const notificationContent = {
				notification: {
					title: notificationTitle,
					body: notificationMessage,
					icon: "default"
				},
				data: {
					"OPEN_FRAGMENT": "COMMS"
				}
			};

			return admin.messaging().sendToDevice(tokenId, notificationContent).then(result => {
				console.log("Notification sent!");
				return result;
				//admin.firestore().collection("notifications").doc(userEmail).collection("userNotifications").doc(notificationId).delete();
			});
		});
  /*admin.messaging().send(payload)
  .then((response) => {
    // Response is a message ID string.
    console.log('Successfully sent message:', response);
    return null;
  })
  .catch((error) => {
    console.log('Error sending message:', error);
  });*/
});
