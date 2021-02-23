export interface HealthPlugin {
  isAvailable(): Promise<boolean>;
  requestAuth(): Promise<boolean>;
  query(): Promise<any>;
  store(): Promise<any>;
}
