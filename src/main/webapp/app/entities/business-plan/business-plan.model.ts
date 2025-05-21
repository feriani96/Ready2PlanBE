export interface IBusinessPlan {
  id: string;
  name?: string | null;
  description?: string | null;
  creationDate?: number | null;
  entropreneurName?: string | null;
}

export type NewBusinessPlan = Omit<IBusinessPlan, 'id'> & { id: null };
