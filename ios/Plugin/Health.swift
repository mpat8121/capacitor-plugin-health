import Foundation
import HealthKit

enum HealthError: Error {
    case failed(message: String)
}
public typealias Measurement = (unit: HKUnit?, type: HKQuantityType?)

@objc public class Health: NSObject {
    @objc public func echo(_ value: String) -> String {
        return value
    }
    
    @objc public func getQuantityType(typeName: String) -> HKSampleType? {
        switch typeName {
            case "height":
                return HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.height)
            case "weight":
                return HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyMass)
            case "leanMass":
                return HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.leanBodyMass)
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
    
    public func getObjectType(typeName: String) -> Measurement {
             var measurement: Measurement = (unit: nil, type: nil)
         switch typeName {
            case "height":
                measurement.unit = HKUnit.init(from: "cm")
                measurement.type =  HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.height)!
            case "weight":
                measurement.unit = HKUnit.init(from: "kg")
                measurement.type = HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyMass)!
            case "leanMass":
                measurement.unit = HKUnit.init(from: "kg")
                measurement.type = HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.leanBodyMass)!
            case "bmi":
                measurement.unit = HKUnit.count()
                measurement.type = HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyMassIndex)!
            case "bodyFat":
                measurement.unit = HKUnit.percent()
                measurement.type = HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyFatPercentage)!
            case "waist":
                measurement.unit = HKUnit.init(from: "cm")
                measurement.type = HKObjectType.quantityType(forIdentifier: HKQuantityTypeIdentifier.waistCircumference)!
        default:
            return measurement;
        }
        return measurement;
    }
    
    @objc public func processResult(results: [HKQuantitySample]) -> [[String: Any]] {
        var output: [[String: Any]] = []
        for result in results {
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
                } else if result.quantity.is(compatibleWith: HKUnit.count()) {
                    unitName = "count"
                    unit = HKUnit.count()
                } else {
                    print("Error: Unknown unit type: ", result.quantity)
                }
                let value = result.quantity.doubleValue(for: unit!)
                output.append(["start": ISO8601DateFormatter().string(from: result.startDate),
                               "end": ISO8601DateFormatter().string(from: result.endDate),
                               "units": unitName!,
                               "value": value
                ])
        }
        return output
    }
}
