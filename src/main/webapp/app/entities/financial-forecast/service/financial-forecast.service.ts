import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IFinancialForecast, NewFinancialForecast } from '../financial-forecast.model';

export type PartialUpdateFinancialForecast = Partial<IFinancialForecast> & Pick<IFinancialForecast, 'id'>;

export type EntityResponseType = HttpResponse<IFinancialForecast>;
export type EntityArrayResponseType = HttpResponse<IFinancialForecast[]>;

@Injectable({ providedIn: 'root' })
export class FinancialForecastService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/financial-forecasts');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/financial-forecasts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(financialForecast: NewFinancialForecast): Observable<EntityResponseType> {
    return this.http.post<IFinancialForecast>(this.resourceUrl, financialForecast, { observe: 'response' });
  }

  update(financialForecast: IFinancialForecast): Observable<EntityResponseType> {
    return this.http.put<IFinancialForecast>(
      `${this.resourceUrl}/${this.getFinancialForecastIdentifier(financialForecast)}`,
      financialForecast,
      { observe: 'response' }
    );
  }

  partialUpdate(financialForecast: PartialUpdateFinancialForecast): Observable<EntityResponseType> {
    return this.http.patch<IFinancialForecast>(
      `${this.resourceUrl}/${this.getFinancialForecastIdentifier(financialForecast)}`,
      financialForecast,
      { observe: 'response' }
    );
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IFinancialForecast>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFinancialForecast[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFinancialForecast[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getFinancialForecastIdentifier(financialForecast: Pick<IFinancialForecast, 'id'>): string {
    return financialForecast.id;
  }

  compareFinancialForecast(o1: Pick<IFinancialForecast, 'id'> | null, o2: Pick<IFinancialForecast, 'id'> | null): boolean {
    return o1 && o2 ? this.getFinancialForecastIdentifier(o1) === this.getFinancialForecastIdentifier(o2) : o1 === o2;
  }

  addFinancialForecastToCollectionIfMissing<Type extends Pick<IFinancialForecast, 'id'>>(
    financialForecastCollection: Type[],
    ...financialForecastsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const financialForecasts: Type[] = financialForecastsToCheck.filter(isPresent);
    if (financialForecasts.length > 0) {
      const financialForecastCollectionIdentifiers = financialForecastCollection.map(
        financialForecastItem => this.getFinancialForecastIdentifier(financialForecastItem)!
      );
      const financialForecastsToAdd = financialForecasts.filter(financialForecastItem => {
        const financialForecastIdentifier = this.getFinancialForecastIdentifier(financialForecastItem);
        if (financialForecastCollectionIdentifiers.includes(financialForecastIdentifier)) {
          return false;
        }
        financialForecastCollectionIdentifiers.push(financialForecastIdentifier);
        return true;
      });
      return [...financialForecastsToAdd, ...financialForecastCollection];
    }
    return financialForecastCollection;
  }
}
