package com.vpn.ui.webview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.webkit.WebSettings.LayoutAlgorithm
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.vpn.base.SetActionBarActivity
import com.vpn.databinding.WebviewActivityH5Binding
import java.io.File
import java.util.*

class WebH5Activity : SetActionBarActivity() {

    private val binding: WebviewActivityH5Binding by lazy { WebviewActivityH5Binding.inflate(layoutInflater) }
    private val TAG: String = javaClass.simpleName
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private val CAMERA_REQUEST_CODE: Int = 1
    private val FILE_CHOOSER_REQUEST_CODE: Int = 2
    private var mFileChooserParams: WebChromeClient.FileChooserParams? = null
    private var progressBar: ProgressBar? = null
    private var mImageFile: File? = null
    private var mImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val url = intent.getStringExtra("url")
        supportActionBar?.hide()
        actionBar?.hide()
        val settings = binding.webH5.settings
        settings.javaScriptEnabled = true //for wa web
        settings.allowContentAccess = true // for camera
        settings.allowFileAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true
        settings.mediaPlaybackRequiresUserGesture = false //for audio messages
        settings.domStorageEnabled = true //for html5 app
        settings.databaseEnabled = true
        settings.setAppCacheEnabled(false) // deprecated
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.saveFormData = true
        settings.loadsImagesAutomatically = true
        settings.blockNetworkImage = false
        settings.blockNetworkLoads = false
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.setNeedInitialFocus(false)
        settings.setGeolocationEnabled(true)
        settings.layoutAlgorithm = LayoutAlgorithm.SINGLE_COLUMN
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        binding.webH5.webViewClient = (object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, urlString: String?): Boolean {
                if(urlString?.startsWith("whatsapp://",false) == true){
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
                        intent.setPackage("com.whatsapp");
                        this@WebH5Activity.startActivity(intent);
                    } catch (e:Exception) { //  没有安装WhatsApp
                        e.printStackTrace();
                    }
                    return true;
                }
                //解决重定向问题
                if (!TextUtils.isEmpty(urlString) && view?.hitTestResult == null) {
                    Log.d(TAG, "shouldOverrideUrlLoading-let=urlString=" + urlString)
                    view?.loadUrl(urlString!!)
                }
                Log.d( TAG, "shouldOverrideUrlLoading-url=" + urlString + ",hitTestResult=" + view?.hitTestResult )
                return super.shouldOverrideUrlLoading(view, urlString)
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun shouldOverrideUrlLoading( view: WebView?, request: WebResourceRequest? ): Boolean {
                if(request?.url?.scheme?.startsWith("whatsapp",false) == true){
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, request.url);
                        intent.setPackage("com.whatsapp");
                        this@WebH5Activity.startActivity(intent);
                    } catch (e:Exception) { //  没有安装WhatsApp
                        e.printStackTrace();
                    }
                    return true;
                }
                //解决重定向问题
                if("about:blank".equals( request?.url.toString())){
                  return true;
                }
                if (!TextUtils.isEmpty(request?.url?.path) && view?.hitTestResult == null) {
                    view?.loadUrl(request?.url?.path!!)
                    Log.d( TAG, "shouldOverrideUrlLoading-N-urlString=" + request?.url + ",Redirect=" + request?.isRedirect )
                    return true;
                }
                Log.d( TAG, "shouldOverrideUrlLoading-N-url=" + request?.url + ",hitTestResult=" + view?.hitTestResult + ",Redirect=" + request?.isRedirect )
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onReceivedError( view: WebView?, errorCode: Int, description: String?, failingUrl: String? ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.d( TAG, "onReceivedError-errorCode=$errorCode,description=$description,failingUrl=$failingUrl" )
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError( view: WebView?, request: WebResourceRequest?, error: WebResourceError? ) {
                super.onReceivedError(view, request, error)
                Log.d( TAG, "onReceivedError-M-errorCode=" + error?.errorCode + ",description=" + error?.description + ",failingUrl=" + request?.url )
            }
        })

        //文件下载监听
        binding.webH5.setDownloadListener(object : DownloadListener {
            override fun onDownloadStart(
                url: String?,
                userAgent: String?,
                contentDisposition: String?,
                mimetype: String?,
                contentLength: Long
            ) {
                //判断url是否有效，且打开默认浏览器下载文件
                if (URLUtil.isValidUrl(url) || Patterns.WEB_URL.matcher(url).matches()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.addCategory(Intent.CATEGORY_BROWSABLE)
                    startActivity(intent)
                } else {
                    Log.d(TAG, "onDownloadStart=无效url=$url")
                }
            }
        })

        binding.webH5.webChromeClient = (object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                //授权资源读取
                runOnUiThread { request?.grant(request.resources) }
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                //顶部进度条控制
                progressBar?.progress = newProgress
                if (newProgress == 100) {
                    progressBar?.postDelayed({ progressBar?.visibility = View.GONE }, 200)
                } else {
                    progressBar?.visibility = View.VISIBLE
                }
            }

            override fun onJsAlert( view: WebView?, url: String?, message: String?, result: JsResult? ): Boolean {
                return true //网页弹框
            }

            override fun onShowFileChooser( webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams? ): Boolean {
                mFilePathCallback = filePathCallback
                mFileChooserParams = fileChooserParams

                //打开文件选择器
                if (ActivityCompat.checkSelfPermission( this@WebH5Activity, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED ) {
                    openFileChooser()
                } else {//请求相机权限
                    ActivityCompat.requestPermissions( this@WebH5Activity, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE )
                }
                Log.d( TAG, "onShowFileChooser=atStr=" + Arrays.toString(fileChooserParams?.acceptTypes) + ",mode=" + fileChooserParams?.mode )
                return true
            }
        })
        binding.webH5.loadUrl(url!!)
    }

    override fun onResume() {
        super.onResume()
        binding.webH5.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webH5.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webH5.destroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.webH5.canGoBack()) {
                binding.webH5.goBack()
                true
            } else super.onKeyDown(keyCode, event)
        } else super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //选择器返回结果处理
        if (mFilePathCallback != null && requestCode == FILE_CHOOSER_REQUEST_CODE) {
            val uris = when {
                (mImageFile?.length()!! > 10) -> {//拍照返回图片文件大小 > 10 byte 则使用
                    arrayOf(mImageUri!!)
                }
                (data?.data != null) -> {//单选文件
                    arrayOf(data.data!!)
                }
                (data?.clipData != null) -> {//多选文件
                    val uriArr = arrayListOf<Uri>()
                    for (i in 0 until data.clipData?.itemCount!!) {
                        val itemAt = data.clipData!!.getItemAt(i).uri
                        uriArr.add(itemAt)
                    }
                    uriArr.toTypedArray()
                }
                else -> {
                    arrayOf()
                }
            }
            mFilePathCallback?.onReceiveValue(uris)
        }
    }

    private fun openFileChooser() {
        val atStr = Arrays.toString(mFileChooserParams?.acceptTypes)

        //文件获取
        val contentIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentIntent.putExtra( Intent.EXTRA_ALLOW_MULTIPLE, mFileChooserParams?.mode == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE )
        val imageCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)//系统相机-拍照
        //设置图片输出路径
        mImageFile = File(filesDir.path, System.currentTimeMillis().toString() + ".jpg")
        mImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile( this, applicationInfo.packageName + ".fileProvider", mImageFile!! )
        } else {
            Uri.fromFile(mImageFile)
        }
        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)

        val videoCaptureIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)//系统相机-拍视频

        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        var intentArr: Array<Intent>? = null
        if ("image/" in atStr) {//图片
            contentIntent.type = "image/*"
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED ) {
                intentArr = arrayOf(imageCaptureIntent)
            }
        } else if ("video/" in atStr) {//视频
            contentIntent.type = "video/*"
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED ) {
                intentArr = arrayOf(videoCaptureIntent)
            }
        } else {//所有文件
            contentIntent.type = "*/*"
            if (ActivityCompat.checkSelfPermission( this, Manifest.permission.CAMERA ) == PackageManager.PERMISSION_GRANTED ) {
                intentArr = arrayOf(imageCaptureIntent, videoCaptureIntent)
            }
        }

        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "选择操作")
        intentArr?.let {
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, it)
        }
        startActivityForResult(chooserIntent, FILE_CHOOSER_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult=requestCode=$requestCode")
        //打开文件选择器
        if (requestCode == CAMERA_REQUEST_CODE) {
            openFileChooser()
        }
    }


}