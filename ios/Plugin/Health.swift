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
            case "fat_percentage":
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
            case "fat_percentage":
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
    
    @objc public func processResult(typeName: String, results: [HKQuantitySample]) -> [String: [Any]] {
        var output: [String: [Any]] = [:]
        var values: [Any] = []
        for result in results {
            var factor: Double?
            var unitName: String?
            var unit: HKUnit?
            if result.quantity.is(compatibleWith: HKUnit.meter()) {
                unitName = "metre"
                unit = HKUnit.meter()
                factor = 0.01
            } else if result.quantity.is(compatibleWith: HKUnit.gram()) {
                unitName = "kilogram"
                unit = HKUnit.gram()
                factor = 1000
            } else if result.quantity.is(compatibleWith: HKUnit.percent()) {
                unitName = "percentage"
                unit = HKUnit.percent()
            } else if result.quantity.is(compatibleWith: HKUnit.count()) {
                unitName = "count"
                unit = HKUnit.count()
            } else {
                print("Error: Unknown unit type: ", result.quantity)
            }
            // Divide by factor - convert grams to Kg for weight, m to cm for waist
            var value: Double
            if(factor != nil) {
                value = result.quantity.doubleValue(for: unit!) / factor!
            } else {
                value = result.quantity.doubleValue(for: unit!)
            }
            values.append([
                "startDate": ISO8601DateFormatter().string(from: result.startDate),
                "endDate": ISO8601DateFormatter().string(from: result.endDate),
                "units": unitName!,
                "value": value,
                "sourceBundleId": "com.apple.Health"
                ])
        }
        output[typeName] = values
        return output
    }

    @objc public func getTypes() -> Set<HKSampleType> {
        var retTypes: Set<HKSampleType> = [];
        retTypes.insert(HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.height)!)
        retTypes.insert(HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyMass)!)
        retTypes.insert(HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.leanBodyMass)!)
        retTypes.insert(HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyMassIndex)!)
        retTypes.insert(HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.bodyFatPercentage)!)
        retTypes.insert(HKQuantityType.quantityType(forIdentifier: HKQuantityTypeIdentifier.waistCircumference)!)
        return retTypes
    }
}
