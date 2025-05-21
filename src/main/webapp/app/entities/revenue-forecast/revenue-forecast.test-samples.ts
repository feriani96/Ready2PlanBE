import { IRevenueForecast, NewRevenueForecast } from './revenue-forecast.model';

export const sampleWithRequiredData: IRevenueForecast = {
  id: '441bdb7e-6d81-4cc4-bde9-2e2c7a5f99fe',
};

export const sampleWithPartialData: IRevenueForecast = {
  id: 'f255dd5c-4219-422c-b62e-5e3613666d01',
  month: 43425,
  unitsSold: 90628,
  totalRevenue: 92993,
};

export const sampleWithFullData: IRevenueForecast = {
  id: '9c2a6587-f45e-425f-8bdd-cc385e2a4d4a',
  month: 49838,
  year: 37733,
  unitsSold: 26798,
  totalRevenue: 5390,
};

export const sampleWithNewData: NewRevenueForecast = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
