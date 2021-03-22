/**
 * @hidden
 */
export interface HealthQueryAllOptions {
  /**
 * (Optional) limit the number of values returned. Defaults to 1000
 */
  limit?: number;
}
/**
 * @hidden
 */
export interface HealthQueryOptions extends HealthQueryAllOptions {
  /**
   * Start date from which to get data
   */
  startDate: Date;
  /**
   * End date from which to get data
   */
  endDate: Date;
  /**
   * Datatype to be queried
   */
  dataType: HealthDataType;
  /**
   * Optional indicator to sort values ascending or descending
   * NOT IMPLEMENTED
   */
  // ascending?: boolean;
  /**
   * In Android, it is possible to query for "raw" steps or to select those as filtered by the Google Fit app.
   * In the latter case the query object must contain the field filtered: true.
   *  NOT IMPLEMENTED
   */
  // filtered?: boolean;
}
/**
 * @hidden
 */
export interface HealthStoreOptions {
  /**
   * Start date from which to get data
   */
  startDate: Date;
  /**
   * End date from which to get data
   */
  endDate: Date;
  /**
   * Datatype to be queried
   */
  dataType: HealthDataType;
  /**
   * Value of corresponding Datatype
   */
  value: string | number;
}
/**
 * @hidden
 */
export interface HealthData {
  /**
   * Start date from which to get data
   */
  startDate: Date;
  /**
   * End date from which to get data
   */
  endDate: Date;
  /**
   * Value of corresponding Datatype
   */
  value: string;
  /**
   * Unit of corresponding value of Datatype
   */
  unit: string;
  /**
   * The source that produced this data. In iOS this is ignored and
   * set automatically to the name of your app.
   */
  sourceName: string;
  /**
   * The complete package of the source that produced this data.
   * In Android, if not specified, it's assigned to the package of the App. In iOS this is ignored and
   * set automatically to the bunde id of the app.
   */
  sourceBundleId: string;
}
/**
 * @hidden
 */
export interface HealthResponse {
  /**
   * Response from plugin call
   */
  success: boolean;
  /**
   * String message
   */
  message: string;
}
/**
 * @hidden
 */
export interface HealthQueryResponse extends HealthResponse {
  /**
   * (iOS/Android) single data type as result
   */
  result: HealthData[];
  /**
   * (ANDROID ONLY) - all data object returns HealthData[]'s under
   * data.weight, data.fat_percentage & data.height
   */
  data?: any;
}
/**
 * @enum available data types for android &/or ios
 */
export enum HealthDataType {
  /**
   * IOS/ANDROID - 'height'
   */
  HEIGHT = 'height',
  /**
  * IOS/ANDROID - 'weight'
  */
  WEIGHT = 'weight',
  /**
  * IOS/ANDROID - 'fat_percentage'
  */
  FAT_PERCENTAGE = 'fat_percentage',
  /**
  * IOS ONLY - 'bmi'
  */
  BMI = 'bmi',
  /**
  * IOS ONLY - 'waist'
  */
  WAIST = 'waist'
}

/**
 * @name Health
 * @description
 * A Capacitor 3 plugin that abstracts fitness and health repositories like Apple HealthKit or Google Fit.
 * 
 * @interfaces
 * HealthQueryAllOptions
 * HealthQueryOptions
 * HealthStoreOptions
 * HealthData
 * HealthResponse
 * HealthQueryResponse
 */
export interface HealthPlugin {
  /**
   * Checks if HealthKit is available
   * @return Promise<HealthResponse>
   * @since 0.0.2
   */
  isAvailable(): Promise<HealthResponse>;
  /**
   * Request authorisation from the user to access Health app data
   * @param data optional (false) for android to prevent default data being sent back
   * @return Promise<HealthResponse>
   * @since 0.0.2
   */
  requestAuth(): Promise<HealthResponse>;
  /**
   * Retrieves data from Health app
   * @param options: HealthOptions
   * @return Promise<HealthQueryResponse>
   * @since 0.0.1
   */
  query(options: HealthQueryOptions): Promise<HealthQueryResponse>;
  /**
   * @description ANDROID ONLY AT THE MOMENT
   * Retrieves data from Health app
   * @param options: HealthOptions
   * @return Promise<HealthQueryResponse>
   * @since 0.0.1
   */
  queryAll(options: HealthQueryAllOptions): Promise<HealthQueryResponse>;
  /**
   * Saves data in Health app
   * @param options: HealthOptions
   * @return Promise<HealthResponse>
   * @since 0.0.2
   */
  store(options: HealthStoreOptions): Promise<HealthResponse>;
}