package com.maxgol.favdish.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.maxgol.favdish.R
import com.maxgol.favdish.application.FavDishApplication
import com.maxgol.favdish.databinding.ActivityAddUpdateDishBinding
import com.maxgol.favdish.databinding.DialogCustomImageSelectionBinding
import com.maxgol.favdish.databinding.DialogCustomListBinding
import com.maxgol.favdish.model.entities.FavDish
import com.maxgol.favdish.utils.Constants
import com.maxgol.favdish.view.adapters.CustomListItemAdapter
import com.maxgol.favdish.viewmodel.FavDishViewModel
import com.maxgol.favdish.viewmodel.FavDishViewModelFactory
import java.io.*
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityAddUpdateDishBinding
    private var mImagePath: String = ""
    private lateinit var mCustomListDialog: Dialog
    private var mFavDishDetails: FavDish? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        if (intent.hasExtra(Constants.EXTRA_DISH_DETAILS)) {
            mFavDishDetails = intent.getParcelableExtra(Constants.EXTRA_DISH_DETAILS)
        }
        setupActionBar()
        mFavDishDetails?.let {
            if (it.id != 0) {
                mImagePath = it.image
                with(mBinding) {
                    Glide.with(this@AddUpdateDishActivity)
                        .load(mImagePath)
                        .centerCrop()
                        .into(ivDishImage)
                    etTitle.setText(it.title)
                    etType.setText(it.type.capitalize(Locale.ROOT))
                    etCategory.setText(it.category)
                    etIngredients.setText(it.ingredients)
                    etDirectionToCook.setText(it.directionToCook)
                    etCookingTime.setText(
                        resources.getString(
                            R.string.lbl_estimate_cooking_time,
                            it.cookingTime
                        )
                    )
                    btnAddDish.text = resources.getString(R.string.lbl_update_dish)
                }
            }
        }
        mBinding.ivAddDishImage.setOnClickListener(this)
        mBinding.etType.setOnClickListener(this)
        mBinding.etCategory.setOnClickListener(this)
        mBinding.etCookingTime.setOnClickListener(this)
        mBinding.btnAddDish.setOnClickListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.let { actionBar ->
            actionBar.title = if (isFavDishIsEditing()) {
                resources.getString(R.string.title_edit_dish)
            } else {
                resources.getString(R.string.title_add_dish)
            }
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.ivAddDishImage -> {
                    customImageSelectionDialog()
                }
                R.id.et_type -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_type),
                        Constants.dishTypes(),
                        Constants.DISH_TYPE
                    )
                }
                R.id.et_category -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_category),
                        Constants.dishCategories(),
                        Constants.DISH_CATEGORY
                    )
                }
                R.id.et_cooking_time -> {
                    customItemsListDialog(
                        resources.getString(R.string.title_select_dish_cooking_time),
                        Constants.dishCookTime(),
                        Constants.DISH_COOKING_TIME
                    )
                }

                R.id.btn_add_dish -> {
                    val title = mBinding.etTitle.prepareText()
                    val type = mBinding.etType.prepareText()
                    val category = mBinding.etCategory.prepareText()
                    val ingredients = mBinding.etIngredients.prepareText()
                    val cookingTimeInMinutes = mBinding.etCookingTime.prepareText()
                    val cookingDirection = mBinding.etDirectionToCook.prepareText()

                    val (messageId, shouldFinishActivity) = when {
                        mImagePath.isEmpty() -> R.string.err_msg_select_dish_image to false
                        title.isEmpty() -> R.string.err_msg_select_dish_title to false
                        type.isEmpty() -> R.string.err_msg_select_dish_type to false
                        category.isEmpty() -> R.string.err_msg_select_dish_category to false
                        ingredients.isEmpty() -> R.string.err_msg_select_dish_ingredients to false
                        cookingTimeInMinutes.isEmpty() -> R.string.err_msg_select_cooking_time to false
                        cookingDirection.isEmpty() -> R.string.err_msg_select_dish_cooking_direction to false
                        else -> {
                            var dishId = 0
                            var imageSource = Constants.DISH_IMAGE_SOURCE_LOCAL
                            var favoriteDishValue = false

                            if (isFavDishIsEditing()) {
                                mFavDishDetails?.let {
                                    dishId = it.id
                                    imageSource = it.imageSource
                                    favoriteDishValue = it.favoriteDish
                                }
                            }

                            val favDishDetails = FavDish(
                                image = mImagePath,
                                imageSource = imageSource,
                                title = title,
                                type = type,
                                category = category,
                                ingredients = ingredients,
                                cookingTime = cookingTimeInMinutes,
                                directionToCook = cookingDirection,
                                favoriteDish = favoriteDishValue,
                                id = dishId
                            )

                            if (dishId == 0) {
                                mFavDishViewModel.insert(favDishDetails)
                                Log.i("Insertion", "Success")
                                R.string.successfully_added to true
                            } else {
                                mFavDishViewModel.update(favDishDetails)
                                R.string.successfully_updated to true
                            }
                        }
                    }
                    showErrorToast(messageId)
                    if (shouldFinishActivity) {
                        finish()
                    }
                }
            }
        }
    }

    private fun isFavDishIsEditing() = mFavDishDetails != null && mFavDishDetails?.id != 0

    private fun EditText.prepareText() = text.toString().trim { it <= ' ' }

    private fun showErrorToast(messageId: Int) {
        Toast.makeText(
            this,
            resources.getString(messageId),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()
            dialog.dismiss()
        }

        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(galleryIntent, GALLERY)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@AddUpdateDishActivity,
                        "You have denied the storage permission to select image",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(
                "It looks like you have turned off permissions " +
                        "required for this feature. It can be enabled under Application Settings"
            ).setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun selectedListItem(item: String, selection: String) {
        when (selection) {
            Constants.DISH_TYPE -> {
                mBinding.etType.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                mBinding.etCategory.setText(item)
            }
            Constants.DISH_COOKING_TIME -> {
                mBinding.etCookingTime.setText(item)
            }
        }
        mCustomListDialog.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.let {
                    val thumbnail: Bitmap? = data.extras?.get("data") as? Bitmap
                    Glide.with(this)
                        .load(thumbnail)
                        .centerCrop()
                        .into(mBinding.ivDishImage)

                    thumbnail?.let {
                        mImagePath = saveImageToInternalStorage(bitmap = thumbnail)
                    }
                    Log.i("ImagePath", mImagePath)

                    mBinding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }

            if (requestCode == GALLERY) {
                data?.let {
                    val selectedPhotoUri = data.data
                    Glide.with(this)
                        .load(selectedPhotoUri)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("TAG", "Error loading image", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap = resource.toBitmap()
                                    mImagePath = saveImageToInternalStorage(bitmap)
                                    Log.i("ImagePath", mImagePath)
                                }
                                return false
                            }
                        })
                        .into(mBinding.ivDishImage)

                    mBinding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vector_edit
                        )
                    )
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("cancelled", "User cancelled image selection")
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemsListDialog(title: String, itemsList: List<String>, selection: String) {
        val binding = DialogCustomListBinding.inflate(layoutInflater).apply {
            tvTitle.text = title
            rvList.layoutManager = LinearLayoutManager(this@AddUpdateDishActivity)
            rvList.adapter = CustomListItemAdapter(this@AddUpdateDishActivity, itemsList, selection)
        }
        mCustomListDialog = Dialog(this).apply { setContentView(binding.root) }
        mCustomListDialog.show()
    }

    companion object {
        private const val CAMERA = 1
        private const val GALLERY = 2

        private const val IMAGE_DIRECTORY = "FavDishImages"
    }
}