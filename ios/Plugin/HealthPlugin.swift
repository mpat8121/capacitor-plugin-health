import Foundation
import Capacitor
import HealthKit

var healthStore: HKHealthStore = HKHealthStore();
// let allTypes = Set([
//             HKObjectType.quantityType(forIdentifier: .height)!,
//             HKObjectType.quantityType(forIdentifier: .bodyMass)!,
//             HKObjectType.quantityType(forIdentifier: .bodyMassIndex)!,
//             HKObjectType.quantityType(forIdentifier: .leanBodyMass)!,
//             HKObjectType.quantityType(forIdentifier: .bodyFatPercentage)!,
//             HKObjectType.quantityType(forIdentifier: .waistCircumference)!
//             ])

@objc(HealthPlugin)
public class HealthPlugin: CAPPlugin {
    private let implementation = Health()

    @objc func isAvailable(_ call: CAPPluginCall) {
        if HKHealthStore.isHealthDataAvailable() {
            call.resolve(["available": true])
        } else {
            call.reject("Health not available")
        }
    }

    @objc func requestAuth(_ call: CAPPluginCall) {
        if(HKHealthStore.isHealthDataAvailable()) {
            let allTypes = implementation.getTypes()
            healthStore.requestAuthorization(toShare: allTypes, read: allTypes) { (success, error) in 
                if !success {
                    call.reject("Unable to authorize")
                    return
                }
                call.resolve(["result": success])
            }
        } else {
            call.reject("Health not available")
        }
    }

    @objc func query(_ call: CAPPluginCall) {
        print(call.options as Any)
        guard let _start = call.getDate("startDate") else {
            call.resolve(["result": false, "message": "Must provide start date"])
            return
        }
        guard let _end = call.getDate("endDate") else {
            call.resolve(["result": false, "message": "Must provide end date"])
            return
        }
        guard let dataType = call.options["dataType"] as? String else {
            return call.resolve(["result": false, "message": "Must provide data type"])
        }
        guard let _limit = call.options["limit"] as? Int else {
            return call.resolve(["result": false, "message": "Must provide limit"])
        }

        let limit: Int = (_limit == 0) ? HKObjectQueryNoLimit : _limit
        
        let predicate = HKQuery.predicateForSamples(withStart: _start, end: _end, options: HKQueryOptions.strictStartDate)

        guard let sampleType: HKSampleType = implementation.getQuantityType(typeName: dataType) else {
                return call.resolve(["result": false, "message": "Error in sample name"])
            }

        let query = HKSampleQuery(sampleType: sampleType, predicate: predicate, limit: limit, sortDescriptors: nil) {
            query, results, error in
            guard let samples = results as? [HKQuantitySample] else {
                call.resolve(["result": false, "message": "Error getting data"])
                return
            }
            let output = self.implementation.processResult(typeName: dataType, results: samples)
            call.resolve([
                "message": "Query data retrieved",
                "result": output
            ])
        }
        healthStore.execute(query)
    }

    @objc func store(_ call: CAPPluginCall) {
        guard var value = call.options["value"] as? Double else {
            call.reject("Must provide a value")
            return
        }
        guard let start = call.getDate("startDate") else {
            call.reject("Must provide start date")
            return
        }
        guard let end = call.getDate("endDate") else {
            call.reject("Must provide end date")
            return
        }
        guard let dataType = call.options["dataType"] as? String else {
            return call.reject("Must provide data type")
        }

        let measurement = implementation.getObjectType(typeName: dataType)
        let measurementType = measurement.type?.identifier
        if (measurementType == "HKQuantityTypeIdentifierBodyFatPercentage" && value >= 1) {
            value = value / 100;
        }
        let newData = HKQuantitySample.init(type: measurement.type!,
                                            quantity: HKQuantity.init(unit: measurement.unit!, doubleValue: value),
        start: start,
        end: end
        )
        healthStore.save(newData) {
            success, error in
            if(error != nil) {
                call.reject("An error occurred")
            }
            call.resolve(["success": success])
        }
    }
}
