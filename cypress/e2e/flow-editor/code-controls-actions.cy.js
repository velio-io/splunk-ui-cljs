/// <reference types="cypress" />

describe('testing actions with code control', () => {
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

    it('coll-where action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-where')
            .click()

        cy.get('.react-flow__node-action')
            .find('.cm-editor')
            .click()
            .type('[:> :metric 10]')
    })

    it('project action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('project')
            .click()

        cy.get('.react-flow__node-action')
            .find('.cm-editor')
            .click()
            .type('[[:> :metric 10]]')
    })

    it('split action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('split')
            .click()

        cy.get('.react-flow__node-action')
            .find('.cm-editor')
            .click()
            .type('[:> :metric 10]')
    })

    it('where action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('where')
            .click()

        cy.get('.react-flow__node-action')
            .find('.cm-editor')
            .click()
            .type('[:> :metric 10]')
    })
})
