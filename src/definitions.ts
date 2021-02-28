/**
 * @hidden
 */
export interface HealthQueryOptions {
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
  dataType: string;
  /**
   * Optional limit the number of values returned. Defaults to 1000
   */
  limit?: number;
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
  dataType: string;
  /**
   * Value of corresponding Datatype
   */
  value: string | number;
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
  sourceBundleId?: string;
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
 * @name Health
 * @description
 * A Capacitor 3  plugin that abstracts fitness and health repositories like Apple HealthKit or Google Fit.
 * 
 * @interfaces
 * HealthQueryOptions
 * HealthStoreOptions
 * HealthData
 */
export interface HealthPlugin {
  /**
   * Checks if HealthKit is available
   * @return Promise<boolean>
   * @since 0.0.1
   */
  isAvailable(): Promise<boolean>;
  /**
   * Request authorisation from the user to access Health app data
   * @return Promise<boolean>
   * @since 0.0.1
   */
  requestAuth(options: {}): Promise<boolean>;
  /**
   * Retrieves data from Health app
   * @param options: HealthOptions
   * @return Promise<HealthData[]>
   * @since 0.0.1
   */
  query(options: HealthQueryOptions): Promise<HealthData[]>;
  /**
   * Saves data in Health app
   * @param options: HealthOptions
   * @return Promise<any>
   * @since 0.0.1
   */
  store(options: HealthStoreOptions): Promise<any>;
}