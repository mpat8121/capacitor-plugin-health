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
  requestAuth(): Promise<boolean>;
  /**
   * Retrieves data from Health app
   * @return Promise<any>
   * @since 0.0.1
  */
  query(): Promise<any>;
  /**
   * Saves data in Health app
   * @return Promise<any>
   * @since 0.0.1
  */
  store(): Promise<any>;
}
