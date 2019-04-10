/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { Observable, of } from 'rxjs';

import { ScholarProjectWebSemanticTestModule } from '../../../test.module';
import { PhotoSempicUpdateComponent } from 'app/entities/photo-sempic/photo-sempic-update.component';
import { PhotoSempicService } from 'app/entities/photo-sempic/photo-sempic.service';
import { PhotoSempic } from 'app/shared/model/photo-sempic.model';

describe('Component Tests', () => {
  describe('PhotoSempic Management Update Component', () => {
    let comp: PhotoSempicUpdateComponent;
    let fixture: ComponentFixture<PhotoSempicUpdateComponent>;
    let service: PhotoSempicService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ScholarProjectWebSemanticTestModule],
        declarations: [PhotoSempicUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(PhotoSempicUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PhotoSempicUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(PhotoSempicService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new PhotoSempic(123);
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
        const entity = new PhotoSempic();
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
