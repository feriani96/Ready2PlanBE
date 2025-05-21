import { IExpenseForecast, NewExpenseForecast } from './expense-forecast.model';

export const sampleWithRequiredData: IExpenseForecast = {
  id: '9b774019-9ad9-4ebd-85ec-1893a15c8590',
};

export const sampleWithPartialData: IExpenseForecast = {
  id: 'a90198cc-34df-4648-a0ee-fa6741d14f56',
};

export const sampleWithFullData: IExpenseForecast = {
  id: 'b099d3b2-b742-4ca9-ac47-0667353f2f5f',
  label: 'cohesive Gloves input',
  monthlyAmount: 12396,
};

export const sampleWithNewData: NewExpenseForecast = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
