package dk.easj.anbo.locationexample

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import dk.easj.anbo.locationexample.databinding.FragmentSecondBinding

class SecondFragment : Fragment() {
    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                        // requestingLocationUpdates = true
                        startLocationUpdates()
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                        // requestingLocationUpdates = true
                        startLocationUpdates()
                    }
                    else -> {
                        // No location access granted.
                        Snackbar.make(
                            binding.secondFragment,
                            "Sorry no location for you",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return // null, means return
                for (location in locationResult.locations) {
                    binding.textviewSecond.append(
                        "\nLoc: " + location.latitude + " " + location.longitude
                    )
                }
            }
        }

        binding.buttonSecond.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create(),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}