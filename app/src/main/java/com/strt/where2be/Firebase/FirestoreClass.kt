package com.strt.where2be.Firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.strt.where2be.Activities.AddLocationActivity
import com.strt.where2be.Activities.BaseActivity
import com.strt.where2be.Activities.CreateFolderActivity
import com.strt.where2be.Activities.LocationsActivity
import com.strt.where2be.Activities.MainActivity
import com.strt.where2be.Activities.ProfileActivity
import com.strt.where2be.Activities.SelectedLocationActivity
import com.strt.where2be.Activities.SignUpActivity
import com.strt.where2be.Activities.SignInActivity
import com.strt.where2be.Models.Folders
import com.strt.where2be.Models.Locations
import com.strt.where2be.Models.User
import com.strt.where2be.Utils.Constants

open class FirestoreClass :BaseActivity(){
    private val mFireStore=FirebaseFirestore.getInstance()
    private val mFireStoreFolders= mFireStore.collection(Constants.USERS).document(getCurrentUserId()).collection(Constants.FOLDERS)
    private lateinit var loggedInUser:User
    private var folderQuantity=0
    private var locationQuantity=0


    //USER DATA HANDLE START
    fun registerUser(activity:SignUpActivity,userinfo: User){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).set(userinfo,SetOptions.merge()).addOnSuccessListener {
            activity.userRegisteredSuccess()
        }.addOnFailureListener { e-> Log.e(activity.javaClass.simpleName,"Error Writing Document") }

    }

    fun updateUserData(activity: ProfileActivity,userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile Data updated")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while creating a board.",e)
                Toast.makeText(activity, "Error when updating the profile!", Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserData(activity: Activity,setupFoldersList:Boolean=false){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get().addOnSuccessListener {document->
            loggedInUser = document.toObject(User::class.java)!!
            when(activity){
                is SignInActivity ->{activity.signInSuccess(loggedInUser)}
                is MainActivity ->{activity.updateNavUserDetails(loggedInUser,setupFoldersList)}
                is ProfileActivity->{activity.setUpProfileDetails(loggedInUser)}

            }
        }.addOnFailureListener { e->
            when(activity){
                is SignInActivity ->{activity.hideProgressDialog()}
                is MainActivity ->{activity.hideProgressDialog()}
                is ProfileActivity ->{activity.hideProgressDialog()}
            }
            Log.e(activity.javaClass.simpleName,"Error Reading Document",e) }
    }

    fun getCurrentUserId():String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        Log.e("currentuser","is $currentUser")
        var currentUserID="HyNnTu0D0Xcff7TGWt8DFVnSaXB2"
        if (currentUser!=null){
            currentUserID=currentUser.uid
        }
        Log.e("current user ıd","is $currentUserID")
        return currentUserID
    }
    //USER DATA HANDLE END
    //FOLDER DATA HANDLE
    fun createFolder(activity: CreateFolderActivity, folderInfo:Folders){
        getFolderSize { count->
            folderQuantity=count
            folderInfo.folder_id=folderQuantity.toString()
            mFireStoreFolders.document(folderQuantity.toString()).set(folderInfo,SetOptions.merge()).addOnSuccessListener {
                activity.folderCreatedSuccessfully()
            }.addOnFailureListener { e-> Log.e(activity.javaClass.simpleName,"Error Writing Document") }
        }
    }

    fun getFolderSize(callback: (Int) -> Unit){
        var count: Int
        mFireStoreFolders.get().addOnSuccessListener { document ->
            count = document.documents.size
            Log.e("Folder Cunt","is $count")
            callback(count)

        }.addOnFailureListener {
            callback(0)
        }
    }
    fun getFolderList(activity: MainActivity){
        mFireStoreFolders.get().addOnSuccessListener {
            document->
            Log.i(activity.javaClass.simpleName,document.documents.toString())
            val foldersList:ArrayList<Folders> = ArrayList()
            for (i in document.documents){
                val folder=i.toObject(Folders::class.java)!!
                folder.folder_id=i.id
                foldersList.add(folder)
            }
            activity.prepareFoldersList(foldersList)
        }.addOnFailureListener {
            e->
            hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating folders list")
        }

    }

    fun getFolderDetails(activity: LocationsActivity, folderDocumentId: String) {
        mFireStoreFolders.document(folderDocumentId).get().addOnSuccessListener {document->
            Log.i(activity.javaClass.simpleName,document.toString())
            activity.folderDetails(document.toObject(Folders::class.java)!!)

        }.addOnFailureListener {

        }
    }
    //FOLDER DATA HANDLE END
    //LOCATIONS DATA HANDLE
    fun createLocation(activity: AddLocationActivity, locationInfo:Locations,folderID:String){
        getLocationSize(folderID) { count->
            locationQuantity=count
            locationInfo.location_id=locationQuantity.toString()
            mFireStoreFolders.document(folderID).collection(Constants.LOCATIONS).document(locationQuantity.toString()).set(locationInfo,SetOptions.merge()).addOnSuccessListener {
                activity.locationCreatedSuccessfully()
            }.addOnFailureListener { e-> Log.e(activity.javaClass.simpleName,"Error Writing Document") }
        }
    }

    fun getLocationSize(folderID: String,callback: (Int) -> Unit){
        var count: Int
        mFireStoreFolders.document(folderID).collection(Constants.LOCATIONS).get().addOnSuccessListener { document ->
            count = document.documents.size
            Log.e("Folder Cunt","is $count")
            callback(count)

        }.addOnFailureListener {
            callback(0)
        }
    }
    fun getLocationList(activity: LocationsActivity,folderID: String){
        mFireStoreFolders.document(folderID).collection(Constants.LOCATIONS).get().addOnSuccessListener {
                document->
            Log.i(activity.javaClass.simpleName,document.documents.toString())
            val locationList:ArrayList<Locations> = ArrayList()
            for (i in document.documents){
                val location=i.toObject(Locations::class.java)!!
                location.location_id=i.id
                locationList.add(location)
            }
            activity.prepareLocationsList(locationList)
        }.addOnFailureListener {
                e->
            hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error while creating folders list")
        }
    }
    fun getLocationDetails(activity: SelectedLocationActivity, folderDocumentId: String,locationId:String) {
        mFireStoreFolders.document(folderDocumentId).collection(Constants.LOCATIONS).document(locationId).get().addOnSuccessListener {document->
            Log.i(activity.javaClass.simpleName,document.toString())
            activity.locationDetails(document.toObject(Locations::class.java)!!)

        }.addOnFailureListener {

        }
    }
}
