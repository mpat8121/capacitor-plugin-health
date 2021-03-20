import { WebPlugin } from '@capacitor/core';

import type { HealthPlugin, HealthData, HealthResponse } from './definitions';

export class HealthWeb extends WebPlugin implements HealthPlugin {
  /**
   * Checks if HealthKit is available
   * @return Promise<HealthResponse>
   * @since 0.0.1
   */
  async isAvailable(): Promise<HealthResponse> {
    throw this.unavailable('Health API not available in this browser.');
  }
  /**
   * Request authorisation from the user to access Health app data
   * @return Promise<HealthResponse>
   * @since 0.0.1
   */
  async requestAuth(): Promise<HealthResponse> {
    throw this.unavailable('Health API not available in this browser.');
  }
   /**
   * Check authorisation from the user to access Health app data
   * @return Promise<HealthResponse>
   * @since 0.0.1
   */
  async checkAuth(): Promise<HealthResponse> {
    throw this.unavailable('Health API not available in this browser.');
  }
  /**
   * Retrieves data from Health app
   * @return Promise<HealthData[]>
   * @since 0.0.1
   */
  async query(): Promise<HealthData[]> {
    throw this.unavailable('Health API not available in this browser.');
  }

  /**
   * Saves data in Health app
   * @return Promise<HealthResponse>
   * @since 0.0.1
   */
  async store(): Promise<HealthResponse> {
    throw this.unavailable('Health API not available in this browser.');
  }
}
