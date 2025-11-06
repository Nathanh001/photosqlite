package com.example.photosqlite

// PhotoAdapter.kt
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhotoAdapter(
    private var photoList: List<Photograph>
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    // 1. ViewHolder: "Sostiene" las vistas de cada ítem
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val descriptionView: TextView = itemView.findViewById(R.id.item_description)
    }

    // 2. Crea nuevas vistas (el LayoutManager lo llama)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    // 3. Vincula los datos con la vista (el LayoutManager lo llama)
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentPhoto = photoList[position]

        // Convertir el ByteArray (BLOB) de vuelta a un Bitmap
        val bitmap = BitmapFactory.decodeByteArray(currentPhoto.image, 0, currentPhoto.image.size)

        holder.imageView.setImageBitmap(bitmap)
        holder.descriptionView.text = currentPhoto.description
    }

    // 4. Devuelve el número total de ítems
    override fun getItemCount() = photoList.size

    // 5. Función para actualizar la lista desde el Activity
    fun updateData(newPhotoList: List<Photograph>) {
        photoList = newPhotoList
        notifyDataSetChanged() // Refresca el RecyclerView
    }
}