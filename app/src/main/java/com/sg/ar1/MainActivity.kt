package com.sg.ar1


import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.CompletableFuture

private const val BOTTOM_SHEET_HIGHT = 50f
private const val DOUBLE_TAP_TOLERANCE_MS =1000L  // if the user tap we wait 1 second to understand that there is only one click, so if you wanr to creat dowbel click you must creat it inside one second

class MainActivity : AppCompatActivity() {

    lateinit var arFragment: ArFragment

    private val models = mutableListOf(
        Model(R.drawable.chair, "Chair", R.raw.chair),
        Model(R.drawable.oven, "Oven", R.raw.oven),
        Model(R.drawable.piano, "Piano", R.raw.piano),
        Model(R.drawable.table, "Table", R.raw.table),
    )

    val viewNodes= mutableListOf<Node>()


    private lateinit var selecteModel: Model
    private fun getCurrentScene() = arFragment.arSceneView.scene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        arFragment = fragment as ArFragment

        setupBottomSheet()
        setupRecyclerView()
        setupDoubleTapArPlaneListener()

        getCurrentScene().addOnUpdateListener {
            rotateViewNodesTowardsUser()
        }
    }



    private fun setupDoubleTapArPlaneListener() {   // sorry its not working
        var firstTapTime = 0L
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            if (firstTapTime == 0L) {
                firstTapTime = System.currentTimeMillis()
            } else if (System.currentTimeMillis() - firstTapTime < DOUBLE_TAP_TOLERANCE_MS) {   //Its mistake to my opionion
                firstTapTime = 0L
                loadModel { modelRenderable, viewRenderable ->
                    addNodeToScene(hitResult.createAnchor(), modelRenderable, viewRenderable)
                }
            } else {
                firstTapTime = System.currentTimeMillis()
            }

        }
    }


    private fun setupRecyclerView() {
        rvModels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvModels.adapter = ModelAdapter(models).apply {
            selectedModel.observe(this@MainActivity, Observer {
                this@MainActivity.selecteModel = it
                val newTitle = "Models (${it.title})"
                tvModel.text = newTitle

            })

        }


    }

    private fun setupBottomSheet() {
        val bottomSheetBeavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBeavior.peekHeight =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                BOTTOM_SHEET_HIGHT,
                resources.displayMetrics
            ).toInt()
        bottomSheetBeavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheet.bringToFront()
            }


        })
    }

    private fun createDeleteButton(): Button {
        return Button(this).apply {
            scaleX=.5f
            scaleY=.5f
            text = "Delete"
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)

        }

    }

    private fun rotateViewNodesTowardsUser(){
        for (node in viewNodes){
            node.renderable?.let{
                val camPos=getCurrentScene().camera.worldPosition
                val viewNodePos=node.worldPosition
                val dir = Vector3.subtract(camPos,viewNodePos)
                node.worldRotation = Quaternion.lookRotation(dir,Vector3.up())
            }
        }
    }

    private fun addNodeToScene(
        anchor: Anchor,
        modelRenewable: ModelRenderable,
        viewRenewable: ViewRenderable
    ) {
        val anchorNode = AnchorNode(anchor)
        val modelNode = TransformableNode(arFragment.transformationSystem).apply {
            renderable = modelRenewable
            setParent(anchorNode)
            getCurrentScene().addChild(anchorNode)
            select()
        }

        val viewNode = Node().apply {
            renderable = null
            setParent(modelNode)
            val box = modelNode.renderable?.collisionShape as Box
            localPosition = Vector3(0f, box.size.y, 0f)
            (viewRenewable.view as Button).setOnClickListener {
                getCurrentScene().removeChild(anchorNode)
                viewNodes.remove(this)
            }
        }

        viewNodes.add(viewNode)

        modelNode.setOnTapListener { _, _ ->
            if (!modelNode.isTransforming) {
                if (viewNode.renderable == null) {
                    viewNode.renderable = viewRenewable
                } else {
                    viewNode.renderable = null
                }

            }

        }

    }

    private fun loadModel(callback: (ModelRenderable, ViewRenderable) -> Unit) {
        val modelRenderable = ModelRenderable.builder()
            .setSource(this, selecteModel.modelResourceId)
            .build()
        val viewRenderable = ViewRenderable.builder()
            .setView(this, createDeleteButton())
            .build()

        CompletableFuture.allOf(modelRenderable, viewRenderable)
            .thenAccept {
                callback(modelRenderable.get(), viewRenderable.get())
            }
            .exceptionally {
                Toast.makeText(this, "Error loading model : $it", Toast.LENGTH_LONG).show()
                null
            }

    }
}