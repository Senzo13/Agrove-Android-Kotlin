package fr.devid.plantR.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import fr.devid.plantR.services.Singleton

class PagerManager(manager: FragmentManager) : FragmentPagerAdapter(manager), ViewPager.OnPageChangeListener {
    private var myFragment = ArrayList<Fragment>()
    private var myPageTitle = ArrayList<String>()
    private var selectedPage = 0
    override fun onPageScrollStateChanged(state: Int) {
    }
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        this.selectedPage = position
    }
    override fun onPageSelected(position: Int) {
        val fragment = this.getItem(position)
        if(position == 1) {
            Singleton.instance.popupSoinsVisibility = true
            println("Ajout de la popup")
        }
        fragment.onResume()
    }

    fun addFragmentPage(Frag: Fragment, Title: String) {
        myFragment.add(Frag)
        myPageTitle.add(Title)
    }
    fun resetAll() {
        myFragment = arrayListOf()
        myPageTitle = arrayListOf()
        selectedPage = 0
    }

    fun rangs() {
        myFragment = arrayListOf()
        myPageTitle = arrayListOf()
        selectedPage = 0
    }
    override fun getItem(position: Int): Fragment {
        println("La position est " + position)
        return myFragment[position]
    }
    override fun getPageTitle(position: Int): CharSequence {
        return myPageTitle[position]
    }
    override fun getCount(): Int {
        return myFragment.size
    }
}