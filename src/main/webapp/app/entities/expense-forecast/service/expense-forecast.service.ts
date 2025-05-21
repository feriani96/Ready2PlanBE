import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IExpenseForecast, NewExpenseForecast } from '../expense-forecast.model';

export type PartialUpdateExpenseForecast = Partial<IExpenseForecast> & Pick<IExpenseForecast, 'id'>;

export type EntityResponseType = HttpResponse<IExpenseForecast>;
export type EntityArrayResponseType = HttpResponse<IExpenseForecast[]>;

@Injectable({ providedIn: 'root' })
export class ExpenseForecastService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/expense-forecasts');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/expense-forecasts');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(expenseForecast: NewExpenseForecast): Observable<EntityResponseType> {
    return this.http.post<IExpenseForecast>(this.resourceUrl, expenseForecast, { observe: 'response' });
  }

  update(expenseForecast: IExpenseForecast): Observable<EntityResponseType> {
    return this.http.put<IExpenseForecast>(`${this.resourceUrl}/${this.getExpenseForecastIdentifier(expenseForecast)}`, expenseForecast, {
      observe: 'response',
    });
  }

  partialUpdate(expenseForecast: PartialUpdateExpenseForecast): Observable<EntityResponseType> {
    return this.http.patch<IExpenseForecast>(`${this.resourceUrl}/${this.getExpenseForecastIdentifier(expenseForecast)}`, expenseForecast, {
      observe: 'response',
    });
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http.get<IExpenseForecast>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IExpenseForecast[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IExpenseForecast[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getExpenseForecastIdentifier(expenseForecast: Pick<IExpenseForecast, 'id'>): string {
    return expenseForecast.id;
  }

  compareExpenseForecast(o1: Pick<IExpenseForecast, 'id'> | null, o2: Pick<IExpenseForecast, 'id'> | null): boolean {
    return o1 && o2 ? this.getExpenseForecastIdentifier(o1) === this.getExpenseForecastIdentifier(o2) : o1 === o2;
  }

  addExpenseForecastToCollectionIfMissing<Type extends Pick<IExpenseForecast, 'id'>>(
    expenseForecastCollection: Type[],
    ...expenseForecastsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const expenseForecasts: Type[] = expenseForecastsToCheck.filter(isPresent);
    if (expenseForecasts.length > 0) {
      const expenseForecastCollectionIdentifiers = expenseForecastCollection.map(
        expenseForecastItem => this.getExpenseForecastIdentifier(expenseForecastItem)!
      );
      const expenseForecastsToAdd = expenseForecasts.filter(expenseForecastItem => {
        const expenseForecastIdentifier = this.getExpenseForecastIdentifier(expenseForecastItem);
        if (expenseForecastCollectionIdentifiers.includes(expenseForecastIdentifier)) {
          return false;
        }
        expenseForecastCollectionIdentifiers.push(expenseForecastIdentifier);
        return true;
      });
      return [...expenseForecastsToAdd, ...expenseForecastCollection];
    }
    return expenseForecastCollection;
  }
}
