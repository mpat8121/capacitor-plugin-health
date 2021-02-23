import { WebPlugin } from '@capacitor/core';

import type { HealthPlugin } from './definitions';

export class HealthWeb extends WebPlugin implements HealthPlugin {
  async isAvailable(): Promise<boolean> {
    throw this.unavailable('Health API not available in this browser.');
  }

  async requestAuth(): Promise<boolean> {
    throw this.unavailable('Health API not available in this browser.');
  }

  async query(): Promise<any> {
    throw this.unavailable('Health API not available in this browser.');
  }

  async store(): Promise<any> {
    throw this.unavailable('Health API not available in this browser.');
  }
}
