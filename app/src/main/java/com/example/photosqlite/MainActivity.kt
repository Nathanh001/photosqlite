package com.example.photosqlite
// MainActivity.kt
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.photosqlite.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    // 1. Referencias
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var photoAdapter: PhotoAdapter

    private var currentImageBitmap: Bitmap? = null

    // Launcher para solicitar permiso de cámara
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                takePictureLauncher.launch(null) // Si se concede el permiso, lanza la cámara
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    // 2. Launcher moderno para la cámara (reemplaza onActivityResult)
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                currentImageBitmap = bitmap
                binding.imgPreview.setImageBitmap(bitmap) // Mostrar vista previa
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configurar ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Inicializar
        dbHelper = DatabaseHelper(this)
        setupRecyclerView()
        loadPhotosFromDB() // Cargar fotos al iniciar

        // 4. Listeners (eventos de clic)
        binding.imgPreview.setOnClickListener {
            // Verificar permisos antes de lanzar la cámara
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permiso ya concedido, lanzar la cámara
                    takePictureLauncher.launch(null)
                }
                else -> {
                    // Pedir permiso
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        binding.btnSalvar.setOnClickListener {
            savePhotoToDB()
        }
    }

    // Configura el RecyclerView con el Adapter
    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(emptyList()) // Empezar con lista vacía
        binding.recyclerViewPhotos.adapter = photoAdapter
        // El layoutManager ya está puesto en el XML (app:layoutManager)
    }

    // Carga los datos de la BD y los pasa al adapter
    private fun loadPhotosFromDB() {
        val photoList = dbHelper.getAllPhotographs()
        photoAdapter.updateData(photoList)
    }

    // Guarda la foto en la base de datos
    private fun savePhotoToDB() {
        val description = binding.etDescripcion.text.toString()

        // Validar
        if (currentImageBitmap == null) {
            Toast.makeText(this, "Por favor, tome una foto", Toast.LENGTH_SHORT).show()
            return
        }
        if (description.isBlank()) {
            Toast.makeText(this, "Por favor, ingrese una descripción", Toast.LENGTH_SHORT).show()
            return
        }

        // Convertir Bitmap (imagen) a ByteArray (BLOB)
        val imageByteArray = bitmapToByteArray(currentImageBitmap!!)

        // Guardar en BD
        dbHelper.addPhotograph(imageByteArray, description)
        Toast.makeText(this, "¡Foto guardada!", Toast.LENGTH_SHORT).show()

        // Limpiar y recargar
        clearFields()
        loadPhotosFromDB()
    }

    // Función de utilidad para convertir Bitmap a ByteArray
    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        // Comprime la imagen. PNG no tiene pérdida, JPEG es más ligero.
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        return stream.toByteArray()
    }

    private fun clearFields() {
        binding.etDescripcion.text.clear()
        binding.imgPreview.setImageResource(android.R.drawable.ic_menu_camera)
        currentImageBitmap = null
    }
}
