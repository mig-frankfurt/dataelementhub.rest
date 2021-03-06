package de.dataelementhub.rest.controller.v1;

import de.dataelementhub.dal.jooq.enums.RelationType;
import de.dataelementhub.model.dto.ElementRelation;
import de.dataelementhub.model.handler.UserHandler;
import de.dataelementhub.model.service.ElementRelationService;
import de.dataelementhub.rest.DataElementHubRestApplication;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Element Relation Controller.
 */
@Transactional
@RestController
@RequestMapping("/v1/relations")
public class ElementRelationController {

  private ElementRelationService elementRelationService;

  private final DSLContext ctx;

  @Autowired
  public ElementRelationController(ElementRelationService elementRelationService, DSLContext ctx) {
    this.elementRelationService = elementRelationService;
    this.ctx = ctx;
  }

  /**
   * Get all sources of the provided type.
   */
  @GetMapping
  public ResponseEntity getElementRelationsByTypes(
      @RequestParam(value = "type", required = false) List<String> types) {
    try {
      List<RelationType> relationTypes = new ArrayList<>();
      if (types != null) {
        for (String type : types) {
          relationTypes.add(RelationType.valueOf(type));
        }
      }
      List<ElementRelation> elementRelations =
          elementRelationService.listByTypes(ctx, relationTypes);
      return new ResponseEntity<>(elementRelations, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>("unknown type", HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Add an element relation.
   */
  @PostMapping
  public ResponseEntity addElementRelation(
      @RequestBody List<de.dataelementhub.dal.jooq.tables.pojos.ElementRelation> elementRelations) {
    try {
      elementRelations.forEach(er -> elementRelationService.createDataElementRelation(ctx,
          UserHandler.getUserByIdentity(ctx, DataElementHubRestApplication.getCurrentUserName())
              .getId(), er));
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (DataAccessException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Update an element relation.
   */
  @PutMapping()
  public ResponseEntity update(@RequestBody
      de.dataelementhub.dal.jooq.tables.pojos.ElementRelation elementRelation) {
    try {
      elementRelationService.updateDataElementRelation(ctx,
          UserHandler.getUserByIdentity(ctx, DataElementHubRestApplication.getCurrentUserName())
              .getId(), elementRelation);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (DataAccessException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  /**
   * Delete an element relation.
   */
  @DeleteMapping()
  public ResponseEntity deleteElementRelation(@RequestBody
      de.dataelementhub.dal.jooq.tables.pojos.ElementRelation elementRelation) {
    try {
      elementRelationService.deleteDataElementRelation(ctx,
          UserHandler.getUserByIdentity(ctx, DataElementHubRestApplication.getCurrentUserName())
              .getId(), elementRelation);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (DataAccessException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
