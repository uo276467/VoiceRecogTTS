package es.uniovi.pulso.practica10.presentacion.busqueda

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.uniovi.pulso.practica10.R
import es.uniovi.pulso.practica10.datos.modelos.Movie
import es.uniovi.pulso.practica10.presentacion.MainActivity
import es.uniovi.pulso.practica10.presentacion.MoviesViewModel
import es.uniovi.pulso.practica10.presentacion.adapters.MovieAdapter

class BusquedaFragment : Fragment() {

    lateinit var viewModel : MoviesViewModel

    private lateinit var recyclerViewMovies : RecyclerView

    private lateinit var movieAdapter : MovieAdapter

    private lateinit var editTextBusqueda : EditText

    private lateinit var buttonBuscar : Button

    private lateinit var buttonEscuchar : AppCompatImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as MainActivity).viewModel
        val root = inflater.inflate(R.layout.fragment_busqueda, container, false)

        recyclerViewMovies = root.findViewById(R.id.recyclerMovies)
        recyclerViewMovies.layoutManager = LinearLayoutManager(context)
        recyclerViewMovies.setHasFixedSize(true)
        movieAdapter = MovieAdapter{mostrarMovie(it)}
        recyclerViewMovies.adapter = movieAdapter

        editTextBusqueda = root.findViewById(R.id.edit_text_busqueda)
        buttonBuscar = root.findViewById(R.id.button_buscar)
        buttonEscuchar = root.findViewById(R.id.button_escuchar)

        buttonBuscar.setOnClickListener {
            viewModel.searchMovies(editTextBusqueda.text.toString())
        }

        buttonEscuchar.setOnClickListener {
            busquedaPorVoz()
        }

        viewModel.foundMovies.observe(viewLifecycleOwner, Observer { listaPeliculas ->
            movieAdapter.update(listaPeliculas)
        })

        return root
    }

    private fun busquedaPorVoz(){
        //Inicializa la actividad de búsqueda por Voz.
        var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
            "¿Qué película está buscando?"
        )
        //No quiero complicaros el código, por eso utilizo el deprecated.
        // Alternativa: registerForActivityResult
        //https://developer.android.com/training/basics/intents/result?hl=es-419

        startActivityForResult(intent,3300)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        //Lo ideal sería comprobar con un if que el requestCode es el 3300.
        //Así identificaríamos la actividad de reconocimiento
        //Fíjate que arriba la lanzamos con el 3300.


        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            //EJERCICIO: Mediante data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            //Piensa ¿Por qué es una lista?
            //Recuperamos los resultados de la voz.
            //Recupera el texto y asígnalo a textoEntrada (Es el EditText).
            //NO tienes que hacer la búsqueda. Simplemente pon el texto en el EditText de búsqueda.
            var result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            editTextBusqueda.setText(result?.get(0))
        }
    }

    /**
     * Navegamos al fragmento Details
     */
    private fun mostrarMovie(movie : Movie) {
        val bundle = Bundle()
        bundle.putInt("MOVIE_ID",movie.id)
        findNavController().navigate(
            R.id.action_busquedaFragment_to_detailsFragment,
            bundle
        )
    }

}