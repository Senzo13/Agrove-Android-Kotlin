package fr.devid.plantR.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import fr.devid.plantR.Constants
import fr.devid.plantR.MainActivityBle
import fr.devid.plantR.operation.CharacteristicListFragment
import fr.devid.plantR.ui.about.AboutFragment
import fr.devid.plantR.ui.myTeam.FragmentManageTeam
import fr.devid.plantR.ui.home.*
import fr.devid.plantR.ui.irrig.Irrig
import fr.devid.plantR.ui.login.LoginFragment
import fr.devid.plantR.ui.main.MainFragment
import fr.devid.plantR.ui.myPlants.*
import fr.devid.plantR.ui.myPlants.FragmentTwoBePlant
import fr.devid.plantR.ui.stateGardener.PlantrFragment
import fr.devid.plantR.ui.myTasks.FragmentOneTasks
import fr.devid.plantR.ui.myTasks.FragmentTipsForTwoTasks
import fr.devid.plantR.ui.myTasks.FragmentTwoTasks
import fr.devid.plantR.ui.myTasks.TasksFragment
import fr.devid.plantR.ui.myTeam.FragmentOneTeam
import fr.devid.plantR.ui.myTeam.FragmentReglageTeam
import fr.devid.plantR.ui.myTeam.FragmentTwoTeam
import fr.devid.plantR.ui.profile.PolicyFragment
import fr.devid.plantR.ui.profile.ProfileFragment
import fr.devid.plantR.ui.publicAppView.*
import fr.devid.plantR.ui.register.RegisterFragment
import fr.devid.plantR.ui.splash.FragmentBranch
import fr.devid.plantR.ui.splash.SplashFragment
import fr.devid.plantR.ui.stateGardener.Irrigation
import fr.devid.plantR.ui.stateGardener.TipsFragment
import fr.devid.plantR.ui.subscribe.*

@Module
interface FragmentBuildersModule {

    @ContributesAndroidInjector
    fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    fun MainActivityBle(): MainActivityBle

    @ContributesAndroidInjector
    fun contributeIrrigation(): Irrigation

    @ContributesAndroidInjector
    fun contributeFragmentReportBug(): FragmentReportBug

    @ContributesAndroidInjector
    fun contributeFragmentCurrentReport(): FragmentCurrentReport

    @ContributesAndroidInjector
    fun contributeFragmentReglageTeam(): FragmentReglageTeam
    @ContributesAndroidInjector
    fun contributeFragmentTwoBePlant(): FragmentTwoBePlant

    @ContributesAndroidInjector
    fun contributeFragmentTwoRangs(): FragmentTwoRangs

    @ContributesAndroidInjector
    fun contributeFragmentOneRangs(): FragmentOneRangs

    @ContributesAndroidInjector
    fun contributeFragmentClosedReport(): FragmentClosedReport

    @ContributesAndroidInjector
    fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    fun contributeFragmentTipsTasksNuisibles(): FragmentTipsTasksNuisibles

    @ContributesAndroidInjector
    fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    fun contributeIrrig(): Irrig

    @ContributesAndroidInjector
    fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    fun contributeFragmentWishList(): FragmentWishList

    @ContributesAndroidInjector
    fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    fun contributeFragmentTipsTasks(): FragmentTipsTasks

    @ContributesAndroidInjector
    fun contributePlantrFragment(): PlantrFragment

    @ContributesAndroidInjector
    fun contributeFragmentOneTeam(): FragmentOneTeam

    @ContributesAndroidInjector
    fun contributeTipsFragment(): TipsFragment

    @ContributesAndroidInjector
    fun contributeFragmentTwoTeam(): FragmentTwoTeam

    @ContributesAndroidInjector
    fun contributeMesPlantesFragment(): MesPlantesFragment

    @ContributesAndroidInjector
    fun contributeTasksFragment(): TasksFragment

    @ContributesAndroidInjector
    fun contributeFragmentTipsForTwoTasks(): FragmentTipsForTwoTasks

    @ContributesAndroidInjector
    fun contributeFragmentOne(): FragmentOne

    @ContributesAndroidInjector
    fun contributeFragmentOneTasks(): FragmentOneTasks

    @ContributesAndroidInjector
    fun contributeFragmentTwoTasks(): FragmentTwoTasks

    @ContributesAndroidInjector
    fun contributeFragmentTwo(): FragmentTwo

    @ContributesAndroidInjector
    fun contributeFragmentNotifTaskClicked(): FragmentNotifTaskClicked

    @ContributesAndroidInjector
    fun contributeFragmentThree(): FragmentThree

    @ContributesAndroidInjector
    fun contributeFragmentFourth(): FragmentFourth

    @ContributesAndroidInjector
    fun contributeFragmentOneSubscribe(): FragmentOneSubscribe

    @ContributesAndroidInjector
    fun contributeFragmentTwoSubscribe(): FragmentTwoSubscribe

    @ContributesAndroidInjector
    fun contributeFragmentOneSubscribeRangs(): FragmentOneSubscribeRangs

    @ContributesAndroidInjector
    fun contributeFragmentTwoSubscribeRangs(): FragmentTwoSubscribeRangs

    @ContributesAndroidInjector
    fun contributeFragmentThreeSubscribe(): FragmentThreeSubscribe

    @ContributesAndroidInjector
    fun contributeFragmentFourthSubscribe(): FragmentFourthSubscribe

    @ContributesAndroidInjector
    fun contributeFragmentHomePlants(): FragmentHomePlants

    @ContributesAndroidInjector
    fun contributeFragmentBePlant(): FragmentBePlant

    @ContributesAndroidInjector
    fun contributeFragmentManageTeam(): FragmentManageTeam

    @ContributesAndroidInjector
    fun contributeFragmentEditPlant(): FragmentEditPlant

    @ContributesAndroidInjector
    fun contributeFragmentMesPlantesSubscribe(): MesPlantesSubscribeFragment

    @ContributesAndroidInjector
    fun contributeFragmentBranch(): FragmentBranch

    @ContributesAndroidInjector
    fun contributeFragmentBePlantSubscribe(): FragmentBePlantSubscribe

    @ContributesAndroidInjector
    fun contributeFragmentAddPlantesRenameFragment(): AddPlantesRenameFragment

    @ContributesAndroidInjector
    fun contributePolicyFragment(): PolicyFragment
    @ContributesAndroidInjector
    fun contributeFragmentAddGardenerJumelage(): FragmentAddGardenerJumelage

    @ContributesAndroidInjector
    fun contributeFragmentAddGardenerJumelageAdress(): FragmentAddGardenerJumelageAdress

    @ContributesAndroidInjector
    fun contributeFragmentSubscribe(): FragmentSubscribe

    @ContributesAndroidInjector
    fun contributeFragmentGardenerSubscribe(): FragmentGardenerSubscribe

    @ContributesAndroidInjector
    fun contributeFragmentChoicePrincipalGardenType(): FragmentChoicePrincipalGardenType

    @ContributesAndroidInjector
    fun contributeFragmentChoiceGardenType(): FragmentChoiceGardenType

    @ContributesAndroidInjector
    fun contributeFragmentSelectGarden(): FragmentSelectGarden

    @ContributesAndroidInjector
    fun contributeFragmentSelectGardenClassic(): FragmentSelectGardenClassic

    @ContributesAndroidInjector
    fun contributeFragmentAddGardener(): FragmentAddGardener

    @ContributesAndroidInjector
    fun contributeFragmentAddPotGarden(): FragmentAddPotGarden

    @ContributesAndroidInjector
    fun contributeFragmentAddSquareGarden(): FragmentAddSquareGarden
    @ContributesAndroidInjector
    fun contributeFragmentAddPotager(): FragmentAddPotager

}