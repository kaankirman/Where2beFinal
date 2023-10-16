package com.strt.where2be.Activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.strt.where2be.Adapters.FolderItemAdapter
import com.strt.where2be.Firebase.FirestoreClass
import com.strt.where2be.Models.Folders
import com.strt.where2be.Models.User
import com.strt.where2be.R
import com.strt.where2be.Utils.Constants
import com.strt.where2be.databinding.ActivityMainBinding

class MainActivity : BaseActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var tbMain:androidx.appcompat.widget.Toolbar
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding=ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            tbMain=binding.appBarMain.toolbarMainActivity
            setupActionBar()
            binding.nvDrawer.setNavigationItemSelectedListener(this)
            FirestoreClass().loadUserData(this,true)
            binding.appBarMain.addFolderButton.setOnClickListener {
                startActivityForResult(Intent(this,CreateFolderActivity::class.java),
                    CREATE_FOLDER_REQUEST_CODE)
            }


        }

    private fun setupActionBar(){
        setSupportActionBar(tbMain)
        val actionBar=supportActionBar
        if (actionBar!=null){
            actionBar.title=""
            setTypeFace(findViewById(R.id.tv_tb_main))
            actionBar.setDisplayHomeAsUpEnabled(true)
            tbMain.setNavigationIcon(R.drawable.nav_menu_icon)
        }
        tbMain.setNavigationOnClickListener {
            toggleDrawer()
        }
    }
    private fun toggleDrawer(){
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBacktoExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== PROFILE_DATA_CHANGED_RESULT_CODE&&requestCode== PROFILE_DATA_STATE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }else if (resultCode== RESULT_OK&&requestCode== CREATE_FOLDER_REQUEST_CODE){
            FirestoreClass().getFolderList(this)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_profile->{
                val intent=Intent(this,ProfileActivity::class.java)
                startActivityForResult(intent, PROFILE_DATA_STATE_REQUEST_CODE)
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent=Intent(this,IntroActivty::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    fun updateNavUserDetails(user:User,setupFoldersList:Boolean){
        val navUserIV=binding.nvDrawer.getHeaderView(0).findViewById<ImageView>(R.id.nav_iv_user)
        val navUserNameTV=binding.nvDrawer.getHeaderView(0).findViewById<TextView>(R.id.nav_tv_user)
        glideImage(this,user.image,R.drawable.person_dark,navUserIV)
        navUserNameTV.text=user.name

        if (setupFoldersList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getFolderList(this)
        }
    }

    fun prepareFoldersList(foldersList:ArrayList<Folders>){
        hideProgressDialog()
        val rvFolderList=findViewById<RecyclerView>(R.id.rv_folder_list)
        if (foldersList.size>0){
            rvFolderList.visibility=View.VISIBLE
            findViewById<TextView>(R.id.tv_no_folder).visibility=View.GONE

            rvFolderList.layoutManager=LinearLayoutManager(this)
            rvFolderList.setHasFixedSize(true)

            val adapter=FolderItemAdapter(this,foldersList)
            rvFolderList.adapter=adapter

            adapter.setOnClickListener(object:FolderItemAdapter.OnClickListener{
                override fun onClick(position: Int, model: Folders) {
                    val intent=Intent(this@MainActivity,LocationsActivity::class.java)
                    intent.putExtra(Constants.FOLDER_ID,model.folder_id)
                    startActivity(intent)
                }
            })

        }else{
            rvFolderList.visibility=View.GONE
            findViewById<TextView>(R.id.tv_no_folder).visibility=View.VISIBLE

        }
    }
}