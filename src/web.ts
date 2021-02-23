import { WebPlugin } from '@capacitor/core';

import type { HealthPlugin } from './definitions';

export class HealthWeb extends WebPlugin implements HealthPlugin {
  /**
   * Checks if HealthKit is available
   * @return Promise<boolean>
   * @since 0.0.1
   */
  async isAvailable(): Promise<boolean> {
    throw this.unavailable('Health API not available in this browser.');
  }
  /**
   * Request authorisation from the user to access Health app data
   * @return Promise<boolean>
   * @since 0.0.1
   */
  async requestAuth(): Promise<boolean> {
    throw this.unavailable('Health API not available in this browser.');
  }
  /**
   * Retrieves data from Health app
   * @return Promise<any>
   * @since 0.0.1
   */
  async query(): Promise<any> {
    throw this.unavailable('Health API not available in this browser.');
  }

  /**
   * Saves data in Health app
   * @return Promise<any>
   * @since 0.0.1
   */
  async store(): Promise<any> {
    throw this.unavailable('Health API not available in this browser.');
  }
}
