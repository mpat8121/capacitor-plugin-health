<h3 align="center">Health</h3>
<p align="center">Uses Capacitor 3.0</p>

<p align="center">
Capacitor 3.0 plugin for Android and iOS that 
allows data to be sent and retrieved from Google Fit or Apple Health
</p>

<p align="center">
  <img src="https://img.shields.io/maintenance/yes/2021?style=flat-square" />
  <a href="https://github.com/mpat8121/capacitor-plugin-filesharer/actions?query=workflow%3A%22CI%22"><img src="https://img.shields.io/github/workflow/status/mpat8121/capacitor-plugin-filesharer/CI?style=flat-square" /></a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-filesharer"><img src="https://img.shields.io/npm/l/capacitor-plugin-filesharer?style=flat-square" /></a>
<br>
  <a href="https://www.npmjs.com/package/capacitor-plugin-filesharer"><img src="https://img.shields.io/npm/dw/capacitor-plugin-filesharer?style=flat-square" /></a>
  <a href="https://www.npmjs.com/package/capacitor-plugin-filesharer"><img src="https://img.shields.io/npm/v/capacitor-plugin-filesharer?style=flat-square" /></a>
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
<a href="#contributors"><img src="https://img.shields.io/badge/all%20contributors-2-orange?style=flat-square" /></a>
<!-- ALL-CONTRIBUTORS-BADGE:END -->
</p>

| Maintainer        | GitHub                                    | Social |
| ----------------- | ----------------------------------------- | ------------------------------------------------------- |
| Mick Patterson    | [mpat8121](https://github.com/mpat8121)   | [@Mick_Patterson_](https://twitter.com/Mick_Patterson_) |
| G. Starr          | [g-starr](https://github.com/g-starr)     |     

## Install

```bash
npm install capacitor-plugin-health
npm run build or ionic build
npx cap sync
npx cap add android
npx cap add ios
```

## API

<docgen-index>

* [`isAvailable()`](#isavailable)
* [`requestAuth()`](#requestauth)
* [`query(...)`](#query)
* [`queryAll(...)`](#queryall)
* [`store(...)`](#store)
* [Interfaces](#interfaces)
* [Enums](#enums)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### isAvailable()

```typescript
isAvailable() => any
```

Checks if HealthKit is available

**Returns:** <code>any</code>

**Since:** 0.0.2

--------------------


### requestAuth()

```typescript
requestAuth() => any
```

Request authorisation from the user to access Health app data

**Returns:** <code>any</code>

**Since:** 0.0.2

--------------------


### query(...)

```typescript
query(options: HealthQueryOptions) => any
```

Retrieves data from Health app

| Param         | Type                                                              | Description     |
| ------------- | ----------------------------------------------------------------- | --------------- |
| **`options`** | <code><a href="#healthqueryoptions">HealthQueryOptions</a></code> | : HealthOptions |

**Returns:** <code>any</code>

**Since:** 0.0.1

--------------------


### queryAll(...)

```typescript
queryAll(options: HealthQueryAllOptions) => any
```

| Param         | Type                                                                    | Description     |
| ------------- | ----------------------------------------------------------------------- | --------------- |
| **`options`** | <code><a href="#healthqueryalloptions">HealthQueryAllOptions</a></code> | : HealthOptions |

**Returns:** <code>any</code>

**Since:** 0.0.1

--------------------


### store(...)

```typescript
store(options: HealthStoreOptions) => any
```

Saves data in Health app

| Param         | Type                                                              | Description     |
| ------------- | ----------------------------------------------------------------- | --------------- |
| **`options`** | <code><a href="#healthstoreoptions">HealthStoreOptions</a></code> | : HealthOptions |

**Returns:** <code>any</code>

**Since:** 0.0.2

--------------------


### Interfaces


#### HealthResponse

| Prop          | Type                 | Description               |
| ------------- | -------------------- | ------------------------- |
| **`success`** | <code>boolean</code> | Response from plugin call |
| **`message`** | <code>string</code>  | String message            |


#### HealthQueryOptions

| Prop            | Type                                                      | Description                       |
| --------------- | --------------------------------------------------------- | --------------------------------- |
| **`startDate`** | <code>any</code>                                          | Start date from which to get data |
| **`endDate`**   | <code>any</code>                                          | End date from which to get data   |
| **`dataType`**  | <code><a href="#healthdatatype">HealthDataType</a></code> | Datatype to be queried            |


#### HealthQueryResponse

| Prop         | Type             | Description                                                                                                  |
| ------------ | ---------------- | ------------------------------------------------------------------------------------------------------------ |
| **`result`** | <code>{}</code>  | (iOS/Android) single data type as result                                                                     |
| **`data`**   | <code>any</code> | (ANDROID ONLY) - all data object returns HealthData[]'s under data.weight, data.fat_percentage & data.height |


#### HealthData

| Prop                 | Type                | Description                                                                                                                                                                                                 |
| -------------------- | ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`startDate`**      | <code>any</code>    | Start date from which to get data                                                                                                                                                                           |
| **`endDate`**        | <code>any</code>    | End date from which to get data                                                                                                                                                                             |
| **`value`**          | <code>string</code> | Value of corresponding Datatype                                                                                                                                                                             |
| **`unit`**           | <code>string</code> | Unit of corresponding value of Datatype                                                                                                                                                                     |
| **`sourceName`**     | <code>string</code> | The source that produced this data. In iOS this is ignored and set automatically to the name of your app.                                                                                                   |
| **`sourceBundleId`** | <code>string</code> | The complete package of the source that produced this data. In Android, if not specified, it's assigned to the package of the App. In iOS this is ignored and set automatically to the bunde id of the app. |


#### HealthQueryAllOptions

| Prop        | Type                | Description                                                      |
| ----------- | ------------------- | ---------------------------------------------------------------- |
| **`limit`** | <code>number</code> | (Optional) limit the number of values returned. Defaults to 1000 |


#### HealthStoreOptions

| Prop            | Type                                                      | Description                       |
| --------------- | --------------------------------------------------------- | --------------------------------- |
| **`startDate`** | <code>any</code>                                          | Start date from which to get data |
| **`endDate`**   | <code>any</code>                                          | End date from which to get data   |
| **`dataType`**  | <code><a href="#healthdatatype">HealthDataType</a></code> | Datatype to be queried            |
| **`value`**     | <code>string \| number</code>                             | Value of corresponding Datatype   |


### Enums


#### HealthDataType

| Members              | Value                         | Description                    |
| -------------------- | ----------------------------- | ------------------------------ |
| **`HEIGHT`**         | <code>'height'</code>         | IOS/ANDROID - 'height'         |
| **`WEIGHT`**         | <code>'weight'</code>         | IOS/ANDROID - 'weight'         |
| **`FAT_PERCENTAGE`** | <code>'fat_percentage'</code> | IOS/ANDROID - 'fat_percentage' |
| **`BMI`**            | <code>'bmi'</code>            | IOS ONLY - 'bmi'               |
| **`WAIST`**          | <code>'waist'</code>          | IOS ONLY - 'waist'             |

</docgen-api>

## Supported Methods

| Name                    | Android | iOS | Web |
| :---------------------- | :------ | :-- | :-- |
| isAvailable                   | ✅      | ✅ | ❌ |
| requestAuth           | ✅      | ✅ | ❌ |
| query           | ✅      | ✅ | ❌ |
| queryAll           | ✅      | ❌ | ❌ |
| store           | ✅      | ✅ | ❌ |

## Contributors

<table>
  <tr>
    <td align="center"><a href="https://github.com/mpat8121"><img src="https://avatars3.githubusercontent.com/u/6610593?v=4" width="100px;" alt=""/><br /><sub><b>Mick Patterson</b></sub></a><br /><a href="https://github.com/mpat8121/capacitor-plugin-filesharer/commits?author=mpat8121" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/g-starr"><img src="https://avatars.githubusercontent.com/u/79553652?s=460&v=4" width="100px;" alt=""/><br /><sub><b>Graham Starr</b></sub></a><br /><a href="https://github.com/mpat8121/capacitor-plugin-filesharer/commits?author=Graei" title="Code">💻</a></td>    
  </tr>
</table>

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!