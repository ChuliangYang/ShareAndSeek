'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
var express = require('express');


admin.initializeApp(functions.config().firebase);

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.countReviews = functions.database.ref('/commercial/reviews/{commercialId}/{pushId}/name')
	.onWrite(event => {
		var db = admin.database();
		var ref = db.ref("/commercial/reviews/statistic")
		const countRef = ref.child(event.params.commercialId).child('reviews_count')
		const listReviewRef = db.ref("/commercial/base_list").child(event.params.commercialId).child('review_count')


		var newCurrent
			// const collectionRef = event.data.ref.parent;
			// const countRef = collectionRef.parent.child('reviews_count');

		// Return the promise from countRef.transaction() so our function 
		// waits for this async event to complete before it exits.
		return countRef.transaction(current => {
			if (event.data.exists() && !event.data.previous.exists()) {
				newCurrent = (current || 0) + 1
				listReviewRef.set(newCurrent)
				return newCurrent;
			} else if (!event.data.exists() && event.data.previous.exists()) {
				newCurrent = (current || 0) - 1
				listReviewRef.set(newCurrent)
				return newCurrent;
			}
		}).then(() => {
			console.log('Counter updated.');
		});
	});


exports.countTotalRating = functions.database.ref('/commercial/reviews/{commercialId}/{pushId}/rate')
	.onWrite(event => {
		var db = admin.database();
		var ref = db.ref("/commercial/reviews/statistic")
		const ratingRef = ref.child(event.params.commercialId).child('total_rating')

		const listTotalRatingRef = db.ref("/commercial/base_list").child(event.params.commercialId).child('rating')


		return ratingRef.transaction(current => {
			if (event.data.exists() && !event.data.previous.exists()) {
				var newValue = (current || 0) + Number(event.data.val())
				listTotalRatingRef.set(newValue)
				return newValue;


				// return parseFloat((current || 0)) + parseFloat(event.data);
			} else if (!event.data.exists() && event.data.previous.exists()) {

				var newValue = (current || 0) - Number(event.data.previous.val())
				listTotalRatingRef.set(newValue)
				return newValue;

			}
		})
	});
// If the number of likes gets deleted, recount the number of likes
exports.recountReviews = functions.database.ref('/commercial/reviews/statistic/{commercialId}/reviews_count').onWrite(event => {
	if (!event.data.exists()) {
		const counterRef = event.data.ref;
		var db = admin.database();
		const collectionRef = db.ref("/commercial/reviews").child(event.params.commercialId)

		// Return the promise from counterRef.set() so our function 
		// waits for this async event to complete before it exits.
		return collectionRef.once('value')
			.then(messagesData => {
				if(Number(messagesData.numChildren())!=0){
				counterRef.set(Number(messagesData.numChildren()))
				}
			});
	}
});


let app = express();
//   commercial_list/sort?category=Restaurants&&type=costume&&district=New_York&&order=desc&&latitude=100&&longitude=200
app.get('/sort', function(req, res) {
	var db = admin.database();
	var ref = db.ref("/commercial/base_list");
	var placeList = new Array();
	var category = req.query.category;


	if(typeof(category) != "undefined" && category != null){
		ref.orderByChild("category").equalTo(category).once("value", function(snapshot) {
		snapshot.forEach(function(childSnapshot) {
			var childKey = childSnapshot.key
			var childData = childSnapshot.val()
			childData.commercialId=childKey;
            childData.totalRating=Number(childData.rating)
            var rating = Number(childData.rating) / childData.review_count
			if (!isNaN(rating)) {
				childData.rating = rating;
			} else {
				childData.rating = Number(0);
            }
			placeList.push(childData);

		});

		var type = req.query.type;
		var order = req.query.order
		switch (type) {
			case 'costume':
				placeList.sort(function(a, b) {
					return a.average_costume - b.average_costume
				})
				if (order == 'desc') {
					placeList.reverse()
				}
				break;
			case 'distance':
				var latitude = req.query.latitude
				var longitude = req.query.longitude
				placeList.sort(function(a, b) {
                    return getFlatternDistance(Number(latitude), Number(longitude), Number(a.location.latitude), Number(a.location.longitude)) - getFlatternDistance(Number(latitude), Number(longitude), Number(b.location.latitude),Number(b.location.longitude));
				})
				if (order == 'desc') {
					placeList.reverse();
				}
				break;
			case 'rating':
                placeList.sort(function(a, b) {
					return Number(a.rating) - b.rating
				})

                if (order == 'desc') {
					placeList.reverse();
				}

				break
            default:
				placeList.sort(function(a, b) {
					return Number(a.create_time) - b.create_time
				})
				if (order == 'desc') {
					placeList.reverse();
				}
				break;

		}

		var district = req.query.district

		if (typeof(district) != "undefined" && district != null) {
			// district = district.replace(/_/g, ' ')

			placeList = placeList.filter(function(a) {
				return a.location.city == district
			})
		}



		res.status(200).send(JSON.stringify(placeList));
	});
	}else{
		ref.once("value", function(snapshot) {
		snapshot.forEach(function(childSnapshot) {
			var childKey = childSnapshot.key
			var childData = childSnapshot.val()
            childData.totalRating=Number(childData.rating)
			var rating = Number(childData.rating) / childData.review_count
			childData.commercialId=childKey;
			if (!isNaN(rating)) {
				childData.rating = rating;
			} else {
				childData.rating = Number(0);
			}
			placeList.push(childData);

		});

		var type = req.query.type;
		var order = req.query.order
		switch (type) {
			case 'costume':
				placeList.sort(function(a, b) {
					return a.average_costume - b.average_costume
				})
				if (order == 'desc') {
					placeList.reverse()
				}
				break;
			case 'distance':
				var latitude = req.query.latitude
				var longitude = req.query.longitude

				placeList.sort(function(a, b) {
                    return getFlatternDistance(Number(latitude), Number(longitude), Number(a.location.latitude), Number(a.location.longitude)) - getFlatternDistance(Number(latitude), Number(longitude), Number(b.location.latitude),Number(b.location.longitude));
				})
				if (order == 'desc') {
					placeList.reverse();
				}
				break;
			case 'rating':
				placeList.sort(function(a, b) {
					return a.rating - b.rating
				})
				if (order == 'desc') {
					placeList.reverse();
				}
				break
			default:
				break;

		}

		var district = req.query.district

		if (typeof(district) != "undefined" && district != null) {
			// district = district.replace(/_/g, ' ')

			placeList = placeList.filter(function(a) {
				return a.location.city == district
			})
		}



		res.status(200).send(JSON.stringify(placeList));
	});
	}
	



})



var EARTH_RADIUS = 6378137.0; //单位M
var PI = Math.PI;

 function getRad(d){
        return d*PI/180.0;
    }
/**
 * approx distance between two points on earth ellipsoid
 * @param {Object} lat1
 * @param {Object} lng1
 * @param {Object} lat2
 * @param {Object} lng2
 */
function getFlatternDistance(lat1, lng1, lat2, lng2) {
	var f = getRad((lat1 + lat2) / 2);
	var g = getRad((lat1 - lat2) / 2);
	var l = getRad((lng1 - lng2) / 2);

	var sg = Math.sin(g);
	var sl = Math.sin(l);
	var sf = Math.sin(f);

	var s, c, w, r, d, h1, h2;
	var a = EARTH_RADIUS;
	var fl = 1 / 298.257;

	sg = sg * sg;
	sl = sl * sl;
	sf = sf * sf;

	s = sg * (1 - sl) + (1 - sf) * sl;
	c = (1 - sg) * (1 - sl) + sf * sl;

	w = Math.atan(Math.sqrt(s / c));
	r = Math.sqrt(s * c) / w;
	d = 2 * w * a;
	h1 = (3 * r - 1) / 2 / c;
	h2 = (3 * r + 1) / 2 / s;

	return d * (1 + fl * (h1 * sf * (1 - sg) - h2 * (1 - sf) * sg));
}


exports.retriveSortedCommercialList = functions.https.onRequest(app);

let app2 = express();
//   commercial_list/sort?category=Restaurants&&type=costume&&district=New_York&&order=desc&&latitude=100&&longitude=200
app2.get('/sort', function(req, res) {
    var db = admin.database();
    var ref = db.ref("/free/list");
    var placeList = new Array();

        ref.orderByChild("create_time").once("value", function(snapshot) {
            snapshot.forEach(function(childSnapshot) {
                var childKey = childSnapshot.key
                var childData = childSnapshot.val()
                placeList.push(childData);
            });


            var district = req.query.district

            if (typeof(district) != "undefined" && district != null) {

                placeList = placeList.filter(function(a) {
                    return a.location.city == district
                })
            }



            res.status(200).send(JSON.stringify(placeList));
        });
})

exports.retriveFreeList = functions.https.onRequest(app2);

let app3 = express();
//   commercial_list/sort?category=Restaurants&&type=costume&&district=New_York&&order=desc&&latitude=100&&longitude=200
app3.get('/sort', function(req, res) {
    var db = admin.database();
    var ref = db.ref("/help/list");
    var placeList = new Array();

    ref.orderByChild("create_time").once("value", function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
            var childKey = childSnapshot.key
            var childData = childSnapshot.val()
            placeList.push(childData);
        });


        var district = req.query.district

        if (typeof(district) != "undefined" && district != null) {

            placeList = placeList.filter(function(a) {
                return a.location.city == district
            })
        }



        res.status(200).send(JSON.stringify(placeList));
    });
})

exports.retriveHelpList = functions.https.onRequest(app3);



exports.freeServicesClean = functions.https.onRequest((req, res)=>{
	var db = admin.database()
	var ref = db.ref("/free/list")
	const now = Date.now();
	console.log("NOW",now)
	return ref.orderByChild('expiredTime').endAt(now).once('value').then(snapshot=>{
		 const updates = {};
        snapshot.forEach(child => {
          updates[child.key] = null;
        });
        // execute all updates in one go and return the result to end the function
        return ref.update(updates);
	}).then(data=>{
		res.status(200).send("success")
	})
})

exports.HelpServicesClean = functions.https.onRequest((req, res)=>{
	var db = admin.database()
	var ref = db.ref("/help/list")
	const now = Date.now();
	console.log("NOW",now)
	return ref.orderByChild('expiredTime').endAt(now).once('value').then(snapshot=>{
		 const updates = {};
        snapshot.forEach(child => {
          updates[child.key] = null;
        });
        // execute all updates in one go and return the result to end the function
        return ref.update(updates);
	}).then(data=>{
		res.status(200).send("success")
	})
})
