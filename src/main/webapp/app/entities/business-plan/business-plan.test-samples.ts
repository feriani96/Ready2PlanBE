import { IBusinessPlan, NewBusinessPlan } from './business-plan.model';

export const sampleWithRequiredData: IBusinessPlan = {
  id: 'c07d3233-24bf-4616-a533-95d6029c8abc',
};

export const sampleWithPartialData: IBusinessPlan = {
  id: '42073597-9eb1-420d-948c-01593bfce647',
  creationDate: 61835,
};

export const sampleWithFullData: IBusinessPlan = {
  id: 'ea4473f3-c81b-400a-815e-ce43b5dee10a',
  name: 'microchip scale',
  description: 'SSL New invoice',
  creationDate: 24246,
  entropreneurName: 'Oregon Cotton cross-platform',
};

export const sampleWithNewData: NewBusinessPlan = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
