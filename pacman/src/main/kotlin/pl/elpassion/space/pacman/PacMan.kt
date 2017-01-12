package pl.elpassion.space.pacman

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.indoorway.android.common.sdk.exceptions.MissingPermissionException
import com.indoorway.android.common.sdk.model.Coordinates
import com.indoorway.android.common.sdk.model.IndoorwayPosition
import com.indoorway.android.gles.GLRendererSurfaceView
import com.indoorway.android.location.sdk.IndoorwayLocationSdk
import com.indoorway.android.location.sdk.exceptions.bluetooth.BLENotSupportedException
import com.indoorway.android.location.sdk.exceptions.bluetooth.BluetoothDisabledException
import com.indoorway.android.location.sdk.exceptions.location.LocationDisabledException
import com.indoorway.android.location.sdk.service.PositioningServiceConnection
import com.indoorway.android.map.sdk.view.IndoorwayMapView
import com.indoorway.android.map.sdk.view.drawable.figures.DrawableCircle
import com.indoorway.android.map.sdk.view.drawable.textures.DrawableTexture
import kotlinx.android.synthetic.main.pac_man_activity.*
import pl.elpassion.space.pacman.model.Size
import pl.elpassion.space.pacman.model.SpriteRect
import pl.elpassion.space.pacman.model.SpriteSheet

val SPRITE_SHEET_ID = "sprite-sheet"
val pacMan = SpriteSheet(SPRITE_SHEET_ID, SpriteRect(144, 64, 100, 128), Size(980f, 640f))

class PacMan : AppCompatActivity() {

    val REQUEST_PERMISSION_CODE = 1
    var currentPosition: IndoorwayPosition? = null
    var serviceConnection: PositioningServiceConnection? = null
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pac_man_activity)
        mapView.apply {
            setOnMapLoadCompletedListener<IndoorwayMapView> {
                mapView.markerControl.apply {
                    // sprite sheet registration
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.sprites)
                    registerTexture(DrawableTexture(SPRITE_SHEET_ID, bitmap))

                    for((idx, point) in TEST_POINTS.withIndex())
                        add(DrawableCircle(idx.toString(), 0.2f, Color.RED, Color.RED, 0.1f, point))
                }
            }
            setOnMapLoadFailedListener<IndoorwayMapView> {

            }
            loadMap(buildingUuid, mapUuid)
        }
        logLocationButton.setOnClickListener { logPosition() }
    }

    override fun onResume() {
        super.onResume()
        serviceConnection = IndoorwayLocationSdk.getInstance().positioningServiceConnection
        startPositioningService()
    }

    override fun onPause() {
        serviceConnection?.stop(this)
        super.onPause()
    }

    private fun startPositioningService() {
        try {
            serviceConnection?.apply {
                setOnPositionChangedListener<PositioningServiceConnection> { position ->
                    currentPosition = position
                    updatePosition(position)
                }
                start(this@PacMan)
            }
        } catch (e: MissingPermissionException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(e.permission), REQUEST_PERMISSION_CODE)
            }
        } catch (e: BLENotSupportedException) {
            showDialog(
                    title = "Sorry",
                    message = "Bluetooth Low Energy is not supported on your device. We are unable to find your indoor location.")
        } catch (e: BluetoothDisabledException) {
            showDialog(
                    title = "Please enable bluetooth",
                    message = "In order to find your indoor position, you need to enable bluetooth on your device.",
                    onPositiveButtonClick = {
                        startActivity(Intent().apply { action = android.provider.Settings.ACTION_BLUETOOTH_SETTINGS })
                    },
                    onNegativeButtonClick = {
                        closeDialog()
                    })
        } catch (e: LocationDisabledException) {
            showDialog(
                    title = "Please enable location",
                    message = "In order of finding your indoor position you must enable location in settings.",
                    onPositiveButtonClick = {
                        startActivity(Intent().apply { action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS })
                    },
                    onNegativeButtonClick = {
                        closeDialog()
                    })
        }
    }

    private fun logPosition() {
        Log.i("PositionLogger", currentPosition?.coordinates?.getText() ?: "no position")
    }

    private fun updatePosition(position: IndoorwayPosition) {
        mapView.markerControl.add(pacMan.asDrawable("pacman", position.coordinates, Size(2f, 2.34f)))
        veryBadInvalidate()
        currentLocationView.text = position.coordinates.getText()
    }

    private fun Coordinates.getText() = "lat: $latitude, long: $longitude"

    private fun showDialog(title: String, message: String,
                           onPositiveButtonClick: (() -> Unit)? = null,
                           onNegativeButtonClick: (() -> Unit)? = null) {
        closeDialog()
        alertDialog = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .apply {
                    if (onPositiveButtonClick != null) {
                        setPositiveButton("OK", { dialog, which -> onPositiveButtonClick() })
                    }
                    if (onNegativeButtonClick != null) {
                        setNegativeButton("Cancel", { dialog, which -> onNegativeButtonClick() })
                    }
                }
                .show()
    }

    private fun closeDialog() {
        if (alertDialog != null) {
            alertDialog?.dismiss()
        }
        alertDialog = null
    }

    private fun veryBadInvalidate() {
        val view: GLRendererSurfaceView = mapView.getChildAt(0) as GLRendererSurfaceView
        view.requestRender()
    }

}
