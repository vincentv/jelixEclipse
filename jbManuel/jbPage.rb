require 'jbmObject'

class JbPage < JbmObject
  
  def initialize href , localDir, agent = nil
    super href, localDir
    
    @manuelBasePath = nil
    @saveBasePath = @saveBasePath + "/html/manuel" 
    
    unless agent.nil?
      @agent = agent
    end
  end
  
  def parse link = nil
    rPage = super link
    href = (link.nil?)?@href:link
    
    unless href.match(/\/articles\/manuel\/(.*)$/).nil?
      content = getContent(rPage)
      
      lPage = File.read('default.tpl')
      lPage = lPage.gsub(/\{Title\}/, title(rPage))
      
      unless content.nil?
        hr = Regexp.new("(<hr noshade=\"noshade\" size=\"1\" />.*</ul>)", Regexp::MULTILINE)
        content = cleanPage(content)
        content = content.gsub(hr, '')
        lPage = lPage.gsub(/\{Content\}/, content)
      end
      
      save lPage, @saveBasePath + '/' + localPagePath(href)
    end
    
    rPage.links.each do |link|
      unless  link.href.nil? 
        if link.href.include? "/articles/manuel/" and not link.href.include? '?'
          parse link.href unless @agent.visited? link 
        end
      end
    end  
  end
  
  private
  def title page
    title = page.content.match(/<title>(.*)<\/title>/)[1]
    return title
  end
  
  def cleanContent page
    content = getContent page
    
    page = File.read('default.tpl')
    page = page.gsub(/\{Title\}/, title(page))
    
    unless content.nil?
      hr = Regexp.new("(<hr noshade=\"noshade\" size=\"1\" />.*</ul>)", Regexp::MULTILINE)
      content = cleanPage(content)
      content = content.gsub(hr, '')
      page = page.gsub(/\{Content\}/, content)
    end
    
    return page
  end
end