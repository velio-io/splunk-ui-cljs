/// <reference types="cypress" />

describe('basic streams flow tests', () => {
    beforeEach(() => {
        cy.visit('http://localhost:6006/iframe.html?id=flow--flow-basic&viewMode=story')
    })

    it('no nodes displayed', () => {
        cy.get('.react-flow__node').should('have.length', 0)
    })

    it('create stream', () => {
        cy.get('.react-flow__renderer.react-flow__container').rightclick()
        cy.get('[data-test=menu]')
            .find('[data-test=item]')
            .first()
            .contains('Create new stream')
            .click()

        cy.get('.react-flow__node-stream')
            .should('have.length', 1)
            .find('input[data-test=textbox]')
            .type('new stream')

        cy.get('.react-flow__node-stream')
            .find('button[type=submit]')
            .click()

        cy.get('.react-flow__node-stream')
            .should('have.text', 'new stream')
    })

    it('create action', () => {
        cy.get('.react-flow__renderer.react-flow__container').rightclick()
        cy.get('[data-test=menu]')
            .find('[data-test=item]')
            .contains('Create new action')
            .click()

        cy.get('.react-flow__node-action')
            .should('have.length', 1)
            .find('input[data-test=textbox]')
            .type('new action')

        cy.get('.react-flow__node-action')
            .find('button[data-test=select]')
            .click()

        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('increment')
            .click()

        cy.get('.react-flow__node-action')
            .find('button[type=submit]')
            .click()

        cy.get('.react-flow__node-action')
            .should('have.text', 'new action')
    })
})
