import Foundation
import Capacitor
import HealthKit

var healthStore: HKHealthStore = HKHealthStore();
let allTypes = Set([
            HKObjectType.quantityType(forIdentifier: .height)!,
            HKObjectType.quantityType(forIdentifier: .bodyMass)!,
            HKObjectType.quantityType(forIdentifier: .bodyMassIndex)!,
            HKObjectType.quantityType(forIdentifier: .bodyFatPercentage)!,
            HKObjectType.quantityType(forIdentifier: .waistCircumference)!
            ])
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(HealthPlugin)
public class HealthPlugin: CAPPlugin {
    private let implementation = Health()

    func getType(typeName: String) {
        switch typeName {
            case "height":
                return HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.height)
            case "weight":
                return HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyMass)
            case "bmi":
                return HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyMassIndex)
            case "bodyFat":
                return HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyFatPercentage)
            case "waist":
                return HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.waistCircumference)
        }
    }

    @obj func isAvailable(_ call: CAPPluginCall) {
        if HKHealthStore.isHealthDataAvailable() {
            call.resolve()
        } else {
            call.reject("Health not available")
        }
    }

    @obj func requestAuth(_ call: CAPPlugin) {
        
        if(HKHealthStore.isHealthDataAvailable()) {
            healthStore.requestAuthorization(toShare: allTypes, read: allTypes) { (success, error) in 
                if !success {
                    call.reject("Unable to authorize")
                    return
                }
                call.resolve()
            }
        } else {
            call.reject("Health not available")
        }
    }

    @obj func query(_ call: CAPPlugin) {
        guard let _start = call.options["startDate"] as? String else {
            call.reject("Must provide start date")
            return
        }
        guard let _end = call.options["endDate"] as? String else {
            call.reject("Must provide end date")
            return
        }
        guard let dataType = call.options["dataType"] as? String else {
            return call.reject("Must provide data type")
        }
        guard let _limit = call.options["limit"] as? String else {
            return call.reject("Must provide limit")
        }

        let limit: Int = (_limit == 0) ? HKObjectQueryNoLimit : _limit
        
        let predicate = HKQuery.predicateForSamples(withStart: _start, end: _end, options: HKQueryOptions.strictStartDate)

        guard let sampleType: HKSampleType = getType(sampleName: dataType) else {
            return call.reject("Error in sample name")
        }

        let query = HKSampleQuery(sampleType: sampleType, predicate: nil, limit: limit, sortDescriptors: nil) {
            query, results, error in

            guard let samples = results as? [HKQuantitySample] else {
                return
            }

            call.resolve(samples)
        }

        healthStore.execute(query)
    }

    @obj func store(_ call: CAPPlugin) {
        guard let value = call.options["value"] as? String else {
            call.reject("Must provide a value")
            return
        }
        guard let start = call.options["startDate"] as? String else {
            call.reject("Must provide start date")
            return
        }
        guard let end = call.options["endDate"] as? String else {
            call.reject("Must provide end date")
            return
        }
        guard let dataType = call.options["dataType"] as? String else {
            return call.reject("Must provide data type")
        }
        guard let _limit = call.options["limit"] as? String else {
            return call.reject("Must provide limit")
        }

        let limit: Int = (_limit == 0) ? HKObjectQueryNoLimit : _limit
        
        let predicate = HKQuery.predicateForSamples(withStart: _start, end: _end, options: HKQueryOptions.strictStartDate)

        guard let quantityType: HKSampleType = getType(sampleName: dataType) else {
            return call.reject("Error in sample name")
        }
        let entryData = HKQuantitySample.init(type: quantityType!,
        quantity: HKQuantity.init(unit: HKUnit.pound(), doubleValue: bodyMass),
        start: start,
        end: end)
        healthKitStore.save(entryData) {
            success, error in
            if(error != nil) {
                call.reject(error)
            }
            call.resolve(success)
        }
    }
}
