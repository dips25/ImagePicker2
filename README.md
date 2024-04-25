/*Add the dependency*/

com.github.dips25:ImagePicker2:2.0

/*Open ImagePicker*/

ImagePicker.openImagePicker(this);

/*Add the following to AndroidManifest*/

<provider>
 android:name="androidx.core.content.FileProvider"
            android:authorities="com.example.myimagepickerapp.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true"
            tools:replace="android:authorities">

<meta-data
android:name="android.support.FILE_PROVIDER_PATHS"
android:resource="@xml/file_paths" />

</provider>




