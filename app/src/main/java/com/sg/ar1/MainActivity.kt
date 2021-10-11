package com.sg.ar1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*

private const val BOTTOM_SHEET_HIGHT=50f

class MainActivity : AppCompatActivity() {

    private val models= mutableListOf(
        Model(R.drawable.chair,"Chair",R.raw.chair),
        Model(R.drawable.oven,"Oven",R.raw.oven),
        Model(R.drawable.piano,"Piano",R.raw.piano),
        Model(R.drawable.table,"Table",R.raw.table),


    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBottomSheet()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvModels.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rvModels.adapter=ModelAdapter(models)

    }

    private fun setupBottomSheet() {
        val bottomSheetBeavior=BottomSheetBehavior.from(bottomSheet)
        bottomSheetBeavior.peekHeight=
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                BOTTOM_SHEET_HIGHT,
                resources.displayMetrics
              ).toInt()
        bottomSheetBeavior.addBottomSheetCallback(object :
        BottomSheetBehavior.BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                bottomSheet.bringToFront()
            }


        })
    }

}