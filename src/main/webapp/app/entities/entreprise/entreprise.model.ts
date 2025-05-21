export interface IEntreprise {
  id: string;
  nom_etp?: string | null;
  pays?: string | null;
  telephone?: number | null;
  description?: string | null;
  deviseSize?: number | null;
  devise?: string | null;
}

export type NewEntreprise = Omit<IEntreprise, 'id'> & { id: null };
