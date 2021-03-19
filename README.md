# capacitor-plugin-health

Integrates with Google Fit and Apple Health

## Install

```bash
npm install capacitor-plugin-health
npx cap sync
```

## API

<docgen-index>

* [`isAvailable()`](#isavailable)
* [`checkAuth()`](#checkauth)
* [`requestAuth()`](#requestauth)
* [`query(...)`](#query)
* [`store(...)`](#store)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### isAvailable()

```typescript
isAvailable() => any
```

Checks if HealthKit is available

**Returns:** <code>any</code>

**Since:** 0.0.1

--------------------


### checkAuth()

```typescript
checkAuth() => any
```

Check authorisation from the user to access Health app data

**Returns:** <code>any</code>

**Since:** 0.0.1

--------------------


### requestAuth()

```typescript
requestAuth() => any
```

Request authorisation from the user to access Health app data

**Returns:** <code>any</code>

**Since:** 0.0.1

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


### store(...)

```typescript
store(options: HealthStoreOptions) => any
```

Saves data in Health app

| Param         | Type                                                              | Description     |
| ------------- | ----------------------------------------------------------------- | --------------- |
| **`options`** | <code><a href="#healthstoreoptions">HealthStoreOptions</a></code> | : HealthOptions |

**Returns:** <code>any</code>

**Since:** 0.0.1

--------------------


### Interfaces


#### HealthQueryOptions

| Prop            | Type                | Description                                                    |
| --------------- | ------------------- | -------------------------------------------------------------- |
| **`startDate`** | <code>any</code>    | Start date from which to get data                              |
| **`endDate`**   | <code>any</code>    | End date from which to get data                                |
| **`dataType`**  | <code>string</code> | Datatype to be queried                                         |
| **`limit`**     | <code>number</code> | Optional limit the number of values returned. Defaults to 1000 |


#### HealthData

| Prop                 | Type                | Description                                                                                                                                                                                                 |
| -------------------- | ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`startDate`**      | <code>any</code>    | Start date from which to get data                                                                                                                                                                           |
| **`endDate`**        | <code>any</code>    | End date from which to get data                                                                                                                                                                             |
| **`value`**          | <code>string</code> | Value of corresponding Datatype                                                                                                                                                                             |
| **`unit`**           | <code>string</code> | Unit of corresponding value of Datatype                                                                                                                                                                     |
| **`sourceName`**     | <code>string</code> | The source that produced this data. In iOS this is ignored and set automatically to the name of your app.                                                                                                   |
| **`sourceBundleId`** | <code>string</code> | The complete package of the source that produced this data. In Android, if not specified, it's assigned to the package of the App. In iOS this is ignored and set automatically to the bunde id of the app. |


#### HealthStoreOptions

| Prop                 | Type                          | Description                                                                                               |
| -------------------- | ----------------------------- | --------------------------------------------------------------------------------------------------------- |
| **`startDate`**      | <code>any</code>              | Start date from which to get data                                                                         |
| **`endDate`**        | <code>any</code>              | End date from which to get data                                                                           |
| **`dataType`**       | <code>string</code>           | Datatype to be queried                                                                                    |
| **`value`**          | <code>string \| number</code> | Value of corresponding Datatype                                                                           |
| **`sourceName`**     | <code>string</code>           | The source that produced this data. In iOS this is ignored and set automatically to the name of your app. |
| **`sourceBundleId`** | <code>string</code>           |                                                                                                           |

</docgen-api>
