package com.dicoding.habitapp.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.setting.SettingsActivity
import com.dicoding.habitapp.ui.ViewModelFactory
import com.dicoding.habitapp.ui.add.AddHabitActivity
import com.dicoding.habitapp.ui.detail.DetailHabitActivity
import com.dicoding.habitapp.ui.random.RandomHabitActivity
import com.dicoding.habitapp.utils.Event
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HabitSortType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class HabitListActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var viewModel: HabitListViewModel

    private val habitAdapter: HabitAdapter by lazy {
        HabitAdapter(::onHabitClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val addIntent = Intent(this, AddHabitActivity::class.java)
            startActivity(addIntent)
        }

        //TODO 6 : Initiate RecyclerView with LayoutManager
        recycler = findViewById(R.id.rv_habit)
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.setHasFixedSize(true)
        recycler.adapter = habitAdapter

        initAction()
        setFabClick()

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory).get(HabitListViewModel::class.java)

        //TODO 7 : Submit pagedList to adapter and update database when onCheckChange
        viewModel.habits.observe(this, {
            habitAdapter.submitList(it)
            habitAdapter.notifyDataSetChanged()
        })

        viewModel.snackbarText.observe(this, Observer(this::showSnackBar))

    }

    private fun onHabitClick(habit: Habit) {
        val intent = Intent(this, DetailHabitActivity::class.java)
        intent.putExtra(HABIT_ID, habit.id)
        startActivity(intent)
    }

    //TODO 15 : Fixing bug : Menu not show and SnackBar not show when list is deleted using swipe
    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            findViewById(R.id.coordinator_layout),
            getString(message),
            Snackbar.LENGTH_SHORT
        ).setAction("Undo"){
            viewModel.insert(viewModel.undo.value?.getContentIfNotHandled() as Habit)
        }.show()
    }

    private fun setFabClick() {
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val addIntent = Intent(this, AddHabitActivity::class.java)
            startActivity(addIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_random -> {
                val intent = Intent(this, RandomHabitActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_filter -> {
                showFilteringPopUpMenu()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = findViewById<View>(R.id.action_filter) ?: return
        PopupMenu(this, view).run {
            menuInflater.inflate(R.menu.filter_habits, menu)

            setOnMenuItemClickListener {
                viewModel.filter(
                    when (it.itemId) {
                        R.id.minutes_focus -> HabitSortType.MINUTES_FOCUS
                        R.id.title_name -> HabitSortType.TITLE_NAME
                        else -> HabitSortType.START_TIME
                    }
                )
                true
            }
            show()
        }
    }

    private fun initAction() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val habit = (viewHolder as HabitAdapter.HabitViewHolder).getHabit
                viewModel.deleteHabit(habit)
            }

        })
        itemTouchHelper.attachToRecyclerView(recycler)
    }
}