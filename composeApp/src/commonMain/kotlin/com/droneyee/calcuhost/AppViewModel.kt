package com.droneyee.calcuhost

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppViewModel {

    //-----------------1

    //电池总能量
    private val _totalBatteryEnergy = MutableStateFlow<Double>(0.0)
    val totalBatteryEnergy: StateFlow<Double> get() = _totalBatteryEnergy

    //电池规格-s
    private val _batterySpecPerDrone = MutableStateFlow<Int>(0)
    private val batterySpecPerDrone: StateFlow<Int> get() = _batterySpecPerDrone

    //单块电池规格-mAh
    private val _batteryCapacitymAh = MutableStateFlow<Int>(0)
    private val batteryCapacitymAh: StateFlow<Int> get() = _batteryCapacitymAh

    //电池数量
    private val _batteryCount = MutableStateFlow<Int>(0)
    private val batteryCount: StateFlow<Int> get() = _batteryCount

    //单块电池的平均电压
    private val _averageBatteryVoltage = MutableStateFlow<Double>(0.0)
    private val averageBatteryVoltage: StateFlow<Double> get() = _averageBatteryVoltage

    //电池能量转换效率
    private val _batteryEfficiency = MutableStateFlow<Int>(0)
    private val batteryEfficiency: StateFlow<Int> get() = _batteryEfficiency

    fun setBatteryEfficiency(batteryEfficiency: Int) {
        _batteryEfficiency.value = batteryEfficiency
        calculateeEduranceTime()
    }

    fun setAverageBatteryVoltage(averageBatteryVoltage: Double) {
        _averageBatteryVoltage.value = averageBatteryVoltage
        calculateTotalBatteryEnergy()
    }

    fun setBatteryCount(batteryCount: Int) {
        _batteryCount.value = batteryCount
        calculateTotalBatteryEnergy()
    }

    fun setBatteryCapacitymAh(batteryCapacitymAh: Int) {
        _batteryCapacitymAh.value = batteryCapacitymAh
        calculateTotalBatteryEnergy()
    }

    fun setBatterySpecPerDrone(batterySpecPerDrone: Int) {
        _batterySpecPerDrone.value = batterySpecPerDrone
        calculateTotalBatteryEnergy()
    }

    /**
     * 计算电池总能量
     */
    private fun calculateTotalBatteryEnergy() {
        val result =
            (batterySpecPerDrone.value * batteryCapacitymAh.value * averageBatteryVoltage.value * batteryCount.value) / 1000.0
        _totalBatteryEnergy.value = result
    }


    //-----------------2
    //起飞所需单轴提供的升力
    private val _liftPerMotorAtTakeoff = MutableStateFlow<String>("")
    val liftPerMotorAtTakeoff: StateFlow<String> get() = _liftPerMotorAtTakeoff

    //空机+电池重量
    private val _emptyWeightWithBattery = MutableStateFlow<Double>(0.0)
    private val emptyWeightWithBattery: StateFlow<Double> get() = _emptyWeightWithBattery

    //无人机轴数
    private val _droneMotorCount = MutableStateFlow<Int>(0)
    private val droneMotorCount: StateFlow<Int> get() = _droneMotorCount

    //载荷重量
    private val _payloadWeight = MutableStateFlow<Double>(0.0)
    private val payloadWeight: StateFlow<Double> get() = _payloadWeight

    //设计挂载非标件重量
    private val _designNonStandardMountWeight = MutableStateFlow<Double>(0.0)
    private val designNonStandardMountWeight: StateFlow<Double> get() = _designNonStandardMountWeight

    fun setDesignNonStandardMountWeight(designNonStandardMountWeight: Double) {
        _designNonStandardMountWeight.value = designNonStandardMountWeight
        calculateLiftPerMotorAtTakeoff()
    }

    fun setPayloadWeight(payloadWeight: Double) {
        _payloadWeight.value = payloadWeight
        calculateLiftPerMotorAtTakeoff()
    }

    fun setDroneMotorCount(droneMotorCount: Int) {
        _droneMotorCount.value = droneMotorCount
        calculateLiftPerMotorAtTakeoff()
    }

    fun setEmptyWeightWithBattery(emptyWeightWithBattery: Double) {
        _emptyWeightWithBattery.value = emptyWeightWithBattery
        calculateLiftPerMotorAtTakeoff()
    }

    /**
     * 2.计算起飞所需升力
     */
    private fun calculateLiftPerMotorAtTakeoff() {
        val result =
            (emptyWeightWithBattery.value + payloadWeight.value + designNonStandardMountWeight.value) / if (droneMotorCount.value == 0) 1 else droneMotorCount.value

        println("see: ${result}")
        _liftPerMotorAtTakeoff.value = result.toString()


    }


    //-----------------3

    //根据动力套获得的对应功率值
    private var _powerOutput = MutableStateFlow<Double>(0.0)
    private val powerOutput: StateFlow<Double> get() = _powerOutput

    //无人机机身功耗
    private val _droneBodyPowerConsumption = MutableStateFlow<Double>(0.0)
    private val droneBodyPowerConsumption: StateFlow<Double> get() = _droneBodyPowerConsumption

    //挂载载荷功耗
    private val _mountedPayloadPowerConsumption = MutableStateFlow<Double>(0.0)
    private val mountedPayloadPowerConsumption: StateFlow<Double> get() = _mountedPayloadPowerConsumption

    fun setPower(power: Double) {
        _powerOutput.value = power
        calculateeEduranceTime()
    }

    fun setDroneBodyPowerConsumption(droneBodyPowerConsumption: Double) {
        _droneBodyPowerConsumption.value = droneBodyPowerConsumption
        calculateeEduranceTime()
    }

    fun setMountedPayloadPowerConsumption(mountedPayloadPowerConsumption: Double) {
        _mountedPayloadPowerConsumption.value = mountedPayloadPowerConsumption
        calculateeEduranceTime()
    }


    //-----------------4
    //电池续航时间
    private val _enduranceTime = MutableStateFlow<Double>(0.0)
    val enduranceTime: StateFlow<Double> get() = _enduranceTime

    /**
     * 最终，计算续航时间
     */
    fun calculateeEduranceTime() {
        val result = 60 *
                (totalBatteryEnergy.value / (droneMotorCount.value * powerOutput.value + mountedPayloadPowerConsumption.value + droneBodyPowerConsumption.value)) *
                (batteryEfficiency.value / 100.0)

        println("totalBatteryEnergy: ${totalBatteryEnergy.value}, droneMotorCount: ${droneMotorCount.value}, powerOutput: ${powerOutput.value}, mountedPayloadPowerConsumption: ${mountedPayloadPowerConsumption.value}, droneBodyPowerConsumption: ${droneBodyPowerConsumption.value}, batteryEfficiency: ${batteryEfficiency.value}")

        println("final time ${result}")
        _enduranceTime.value = result
    }

}