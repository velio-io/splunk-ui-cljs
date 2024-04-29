/// <reference types="cypress" />

describe('testing actions without controls', () => {
    beforeEach(() => {
        cy.visit('http://localhost:6006/iframe.html?id=flow--simple-flow&viewMode=story')
            .wait(500)

        cy.get('.react-flow__node-action')
            .click()
            .find('[data-test="edit-action"]')
            .click()

        cy.get('.react-flow__node-action')
            .find('button[data-test=select]')
            .click()

        cy.wait(200)
    })

    afterEach(() => {
        cy.get('.react-flow__node-action')
            .find('button[type=submit]')
            .click()

        cy.get('.react-flow__node-action')
            .should('have.text', 'Test')
    })

    it('coll-count action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-count')
            .click()
    })

    it('coll-increase action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-increase')
            .click()
    })

    it('coll-max action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-max')
            .click()
    })

    it('coll-mean action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-mean')
            .click()
    })

    it('coll-min action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-min')
            .click()
    })

    it('coll-quotient action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-quotient')
            .click()
    })

    it('coll-rate action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-rate')
            .click()
    })

    it('coll-sum action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-sum')
            .click()
    })

    it('critical action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('critical')
            .click()
    })

    it('ddt action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('ddt')
            .click()
    })

    it('ddt-pos action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('ddt-pos')
            .click()
    })

    it('debug action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('debug')
            .click()
    })

    it('decrement action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('decrement')
            .click()
    })

    it('error action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('error')
            .click()
    })

    it('exception-stream action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('exception-stream')
            .click()
    })

    it('expired action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('expired')
            .click()
    })

    it('increment action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('increment')
            .click()
    })

    it('info action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('info')
            .click()
    })

    it('io action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('io')
            .click()
    })

    it('not-expired action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('not-expired')
            .click()
    })

    it('sdo action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('sdo')
            .click()
    })

    it('sflatten action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('sflatten')
            .click()
    })

    it('smax action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('smax')
            .click()
    })

    it('smin action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('smin')
            .click()
    })

    it('test-action action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('test-action')
            .click()
    })

    it('warning action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('warning')
            .click()
    })
})
