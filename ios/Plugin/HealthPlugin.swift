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

    func getType(typeName: String) -> HKSampleType? {
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
        default:
            return nil;
        }
    }

    @objc func isAvailable(_ call: CAPPluginCall) {
        if HKHealthStore.isHealthDataAvailable() {
            call.resolve(["available": true])
        } else {
            call.reject("Health not available")
        }
    }

    @objc func requestAuth(_ call: CAPPluginCall) {
        
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

    @objc func query(_ call: CAPPluginCall) {
        guard let _start = call.options["startDate"] as? Date else {
            call.reject("Must provide start date")
            return
        }
        guard let _end = call.options["endDate"] as? Date else {
            call.reject("Must provide end date")
            return
        }
        guard let dataType = call.options["dataType"] as? String else {
            return call.reject("Must provide data type")
        }
        guard let _limit = call.options["limit"] as? Int else {
            return call.reject("Must provide limit")
        }

        let limit: Int = (_limit == 0) ? HKObjectQueryNoLimit : _limit
        
        let predicate = HKQuery.predicateForSamples(withStart: _start, end: _end, options: HKQueryOptions.strictStartDate)

        guard let sampleType: HKSampleType = getType(typeName: dataType) else {
            return call.reject("Error in sample name")
        }

        let query = HKSampleQuery(sampleType: sampleType, predicate: nil, limit: limit, sortDescriptors: nil) {
            query, results, error in

            guard let samples = results as? [HKQuantitySample] else {
                return
            }
            var output: [[String: Any]] = []
            for result in samples {
                
                var unitName: String?
                var unit: HKUnit?
                if result.quantity.is(compatibleWith: HKUnit.meter()) {
                    unitName = "metre"
                    unit = HKUnit.meter()
                } else if result.quantity.is(compatibleWith: HKUnit.gram()) {
                    unitName = "gram"
                    unit = HKUnit.gram()
                } else if result.quantity.is(compatibleWith: HKUnit.percent()) {
                    unitName = "percentage"
                    unit = HKUnit.percent()
                } else {
                    print("Error: unit type: ", result.quantity)
                }
                let value = result.quantity.doubleValue(for: unit!)
                output.append(["start": ISO8601DateFormatter().string(from: result.startDate),
                               "end": ISO8601DateFormatter().string(from: result.endDate),
                               "units": unitName,
                               "value": value
                ])
            }
            call.resolve([
                "resultCount": output.count,
                "resultData": output
            ])
        }

        healthStore.execute(query)
    }

    @objc func store(_ call: CAPPluginCall) {
        guard let value = call.options["value"] as? Double else {
            call.reject("Must provide a value")
            return
        }
        guard let start = call.options["startDate"] as? Date else {
            call.reject("Must provide start date")
            return
        }
        guard let end = call.options["endDate"] as? Date else {
            call.reject("Must provide end date")
            return
        }
        guard let dataType = call.options["dataType"] as? String else {
            return call.reject("Must provide data type")
        }
        guard let _limit = call.options["limit"] as? Int else {
            return call.reject("Must provide limit")
        }

        let limit: Int = (_limit == 0) ? HKObjectQueryNoLimit : _limit;
        
        let predicate = HKQuery.predicateForSamples(withStart: start, end: end, options: HKQueryOptions.strictStartDate)

        let quantityType = HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyMass)
        let entryData = HKQuantitySample.init(type: quantityType!,
        quantity: HKQuantity.init(unit: HKUnit.pound(), doubleValue: value),
        start: start,
        end: end)
        healthStore.save(entryData) {
            success, error in
            if(error != nil) {
                call.reject("An error occurred")
            }
            call.resolve(["success": success])
        }
    }
}
