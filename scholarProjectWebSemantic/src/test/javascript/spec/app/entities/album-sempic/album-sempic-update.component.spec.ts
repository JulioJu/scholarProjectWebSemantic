/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { ScholarProjectWebSemanticTestModule } from '../../../test.module';
import { AlbumSempicUpdateComponent } from 'app/entities/album-sempic/album-sempic-update.component';
import { AlbumSempicService } from 'app/entities/album-sempic/album-sempic.service';
import { AlbumSempic } from 'app/shared/model/album-sempic.model';

describe('Component Tests', () => {
  describe('AlbumSempic Management Update Component', () => {
    let comp: AlbumSempicUpdateComponent;
    let fixture: ComponentFixture<AlbumSempicUpdateComponent>;
    let service: AlbumSempicService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ScholarProjectWebSemanticTestModule],
        declarations: [AlbumSempicUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(AlbumSempicUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AlbumSempicUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AlbumSempicService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new AlbumSempic(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new AlbumSempic();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
