const functions = require('firebase-functions');
const nodemailer = require('nodemailer');

const APP_NAME = 'Rig2Gig';
const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

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
			const email = result[1].get("email-address"); // The email of the user.
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
		
			sendRequestEmail(email, toUserName, fromUserName);	

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

async function sendRequestEmail(email, receiver, sender) {
  const mailOptions = {
    from: `${APP_NAME} <noreply@firebase.com>`,
    to: email,
  };

  mailOptions.subject = `Contact request received on ${APP_NAME}!`;
  mailOptions.text = `Hey ${receiver || ''}! ${sender} has sent a contact request. Get on the app to accept!.`;
  await mailTransport.sendMail(mailOptions);
  console.log('New request email sent to:', email);
  return null;
}