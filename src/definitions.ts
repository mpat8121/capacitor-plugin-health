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
   * @return Promise<any>
   * @since 0.0.1
   */
  query(options: HealthOptions): Promise<any>;
  /**
   * Saves data in Health app
   * @param options: HealthOptions
   * @return Promise<any>
   * @since 0.0.1
   */
  store(options: HealthOptions): Promise<any>;
}


export interface HealthOptions {
  startDate: Date;
  endDate: Date;
  dataType: string;
  limit?: number;
  value?: any;
  sourceBundleId?: string;
  sourceName?: string;
}